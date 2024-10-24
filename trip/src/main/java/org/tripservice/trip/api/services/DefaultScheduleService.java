package org.tripservice.trip.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DefaultScheduleService implements ScheduleService {

    LocationClient locationClient;

    ScheduleRepository scheduleRepository;

    VariableConfig variableConfig;

    ObjectsValidator<ScheduleRequest> tripScheduleValidator;
    ScheduleMapper scheduleMapper;

    @Override
    public ScheduleDetail createSchedule(ScheduleRequest request) {
        tripScheduleValidator.validate(request);
        var schedule = locationClient.getTripSchedule(request, variableConfig.LOCATION_API_KEY).orElseThrow(
                () -> new DataNotFoundException(List.of("Locations not found"))
        );
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
        newSchedule.setId(id);
        scheduleRepository.save(newSchedule);
        return scheduleMapper.toDetail(newSchedule);
    }

}
