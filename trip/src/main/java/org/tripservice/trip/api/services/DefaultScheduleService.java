package org.tripservice.trip.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.tripservice.trip.api.documents.Schedule;
import org.tripservice.trip.api.documents.VehicleType;
import org.tripservice.trip.api.dtos.schedule.ScheduleDetail;
import org.tripservice.trip.api.dtos.schedule.ScheduleRequest;
import org.tripservice.trip.api.dtos.schedule.ScheduleResponse;
import org.tripservice.trip.api.repositories.ScheduleRepository;
import org.tripservice.trip.api.repositories.VehicleTypeRepository;
import org.tripservice.trip.api.services.interfaces.ScheduleService;
import org.tripservice.trip.api.services.mappers.ScheduleMapper;
import org.tripservice.trip.api.services.mappers.TripMapper;
import org.tripservice.trip.clients.LocationClient;
import org.tripservice.trip.clients.VehicleClient;
import org.tripservice.trip.config.VariableConfig;
import org.tripservice.trip.utils.dtos.ListResponse;
import org.tripservice.trip.utils.exception.DataNotFoundException;
import org.tripservice.trip.utils.services.ObjectsValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DefaultScheduleService implements ScheduleService {

    LocationClient locationClient;
    VehicleClient vehicleClient;

    ScheduleRepository scheduleRepository;

    VehicleTypeRepository vehicleTypeRepository;

    VariableConfig variableConfig;

    ObjectsValidator<ScheduleRequest> tripScheduleValidator;
    private final ScheduleMapper scheduleMapper;
    private final TripMapper tripMapper;

    @Override
    public ScheduleDetail createSchedule(ScheduleRequest request) {
        tripScheduleValidator.validate(request);
        var schedule = locationClient.getTripSchedule(request, variableConfig.LOCATION_API_KEY).orElseThrow(
                () -> new DataNotFoundException(List.of("Locations not found"))
        );

        var vehicleTypeOptional = vehicleTypeRepository.findById(request.getVehicleTypeId());
        VehicleType vehicleType;
        if (vehicleTypeOptional.isEmpty()) {
            vehicleType = vehicleClient.findById(request.getVehicleTypeId(), variableConfig.VEHICLE_API_KEY).orElseThrow(
                    () -> new DataNotFoundException(List.of("Vehicle types not found"))
            );
            vehicleTypeRepository.save(vehicleType);
        } else {
            vehicleType = vehicleTypeOptional.get();
        }
        schedule.setVehicleType(vehicleType);
        schedule.setPrice(request.getPrice());
        scheduleRepository.save(schedule);
        return scheduleMapper.toDetail(schedule);
    }

    @Override
    public ListResponse<ScheduleResponse> getSchedulesByFromAndTo(String from, String to) {
        List<Schedule> schedules;
        if (from == null || to == null) {
            schedules = scheduleRepository.findAll();
        } else {
            schedules = scheduleRepository.findByRegionFromAndRegionTo(from, to);
        }
        return ListResponse.<ScheduleResponse>builder()
                .size(schedules.size())
                .data(schedules.stream().map(scheduleMapper::toResponse).collect(Collectors.toList()))
                .build();
    }


    @Override
    public ScheduleDetail updateSchedule(String id, ScheduleRequest request) {
        if (!scheduleRepository.existsById(id)) {
            throw new DataNotFoundException(List.of("Schedule not found"));
        }
        var newSchedule = locationClient.getTripSchedule(request, variableConfig.LOCATION_API_KEY).orElseThrow(
                () -> new DataNotFoundException(List.of("Locations not found"))
        );
        VehicleType vehicleType;
        var vehicleTypeOptional = vehicleTypeRepository.findById(request.getVehicleTypeId());
        if (vehicleTypeOptional.isEmpty()) {
            vehicleType = vehicleClient.findById(request.getVehicleTypeId(), variableConfig.VEHICLE_API_KEY).orElseThrow(
                    () -> new DataNotFoundException(List.of("Vehicle types not found"))
            );
            vehicleTypeRepository.save(vehicleType);
        } else {
            vehicleType = vehicleTypeOptional.get();
        }
        newSchedule.setVehicleType(vehicleType);
        newSchedule.setPrice(request.getPrice());
        newSchedule.setId(id);
        scheduleRepository.save(newSchedule);
        return scheduleMapper.toDetail(newSchedule);
    }

    public Long roundPrice(double price, int roundTo) {
        return ((long) ((price + roundTo - 1) / roundTo) * roundTo);
    }
}
