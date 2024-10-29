package org.tripservice.trip.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.tripservice.trip.api.documents.Schedule;
import org.tripservice.trip.api.dtos.schedule.ScheduleDetail;
import org.tripservice.trip.api.dtos.schedule.ScheduleRequest;
import org.tripservice.trip.api.dtos.schedule.ScheduleResponse;
import org.tripservice.trip.api.repositories.ScheduleRepository;
import org.tripservice.trip.api.repositories.VehicleTypeRepository;
import org.tripservice.trip.api.services.interfaces.ScheduleService;
import org.tripservice.trip.api.services.mappers.ScheduleMapper;
import org.tripservice.trip.clients.LocationClient;
import org.tripservice.trip.config.VariableConfig;
import org.tripservice.trip.utils.dtos.ListResponse;
import org.tripservice.trip.utils.exception.DataNotFoundException;
import org.tripservice.trip.utils.services.ObjectsValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DefaultScheduleService implements ScheduleService {

    LocationClient locationClient;

    ScheduleRepository scheduleRepository;
    VehicleTypeRepository vehicleTypeRepository;

    VariableConfig variableConfig;

    ObjectsValidator<ScheduleRequest> tripScheduleValidator;
    ScheduleMapper scheduleMapper;

    @Override
    public ScheduleDetail createSchedule(ScheduleRequest request) {
        tripScheduleValidator.validate(request);
        var schedule = locationClient.getTripSchedule(request, variableConfig.LOCATION_API_KEY).orElseThrow(
                () -> new DataNotFoundException(List.of("Locations not found"))
        );
        double roundedUp = roundedUp10(schedule.getDuration());
        schedule.setDuration(roundedUp);
        for(var item : schedule.getPickUps()) {
            item.setDurationToLocation(roundedUp10(item.getDurationToLocation()));
        }
        for(var item : schedule.getTransits()) {
            item.setDurationToLocation(roundedUp10(item.getDurationToLocation()));
        }
        var vehicleType = vehicleTypeRepository.findById(request.getVehicleTypeId()).orElseThrow(
                () -> new DataNotFoundException(List.of("Vehicle types not found"))
        );
        schedule.setVehicleTypeId(vehicleType.getId());
        schedule.setVehicleTypeName(vehicleType.getName());
        schedule.setBookedCount(0L);
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
        var result = schedules.stream()
                .map(scheduleMapper::toResponse)
                .sorted(Comparator.comparing(scheduleResponse -> scheduleResponse.getRegionFrom().getSlug()))
                .collect(Collectors.toList());
        return ListResponse.<ScheduleResponse>builder()
                .size(schedules.size())
                .data(result)
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
        newSchedule.setId(id);
        scheduleRepository.save(newSchedule);
        return scheduleMapper.toDetail(newSchedule);
    }

    @Override
    public List<List<ScheduleResponse>> getPopularSchedule() {
        var popularRegions = List.of("ho-chi-minh", "da-lat", "da-nang");
        List<List<ScheduleResponse>> scheduleResponses = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 3);
        popularRegions.forEach(region -> {
            var schedules = scheduleRepository.findTop3ByRegionFromOrderByBookedCountDesc(region, pageable);
            scheduleResponses.add(
                    schedules.stream().map(scheduleMapper::toResponse).collect(Collectors.toList())
            );
        });
        return scheduleResponses;
    }

    public double roundedUp10(double input) {
        return Math.ceil(input / 10) * 10;
    }
}
