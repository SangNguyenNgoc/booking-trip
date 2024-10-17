package org.tripservice.trip.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.tripservice.trip.api.documents.Schedule;
import org.tripservice.trip.api.dtos.schedule.TripScheduleRequest;
import org.tripservice.trip.api.repositories.ScheduleRepository;
import org.tripservice.trip.api.services.interfaces.ScheduleService;
import org.tripservice.trip.clients.LocationClient;
import org.tripservice.trip.config.VariableConfig;
import org.tripservice.trip.utils.dtos.ListResponse;
import org.tripservice.trip.utils.exception.DataNotFoundException;
import org.tripservice.trip.utils.services.ObjectsValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultScheduleService implements ScheduleService {

    private final LocationClient locationClient;

    private final ScheduleRepository scheduleRepository;

    private final VariableConfig variableConfig;

    private final ObjectsValidator<TripScheduleRequest> tripScheduleValidator;

    @Override
    public Schedule createSchedule(TripScheduleRequest request) {
        tripScheduleValidator.validate(request);
        var schedule = locationClient.getTripSchedule(request, variableConfig.LOCATION_API_KEY).orElseThrow(
                () -> new DataNotFoundException(List.of("Locations not found"))
        );
        scheduleRepository.save(schedule);
        return schedule;
    }

    @Override
    public ListResponse<Schedule> getSchedulesByFromAndTo(String from, String to) {
        List<Schedule> schedules;
        if(from == null || to == null) {
            schedules = scheduleRepository.findAll();
        } else {
            schedules = scheduleRepository.findByFromAndTo(from, to);
        }
        return ListResponse.<Schedule>builder()
                .size(schedules.size())
                .data(schedules)
                .build();
    }

    @Override
    public Schedule updateSchedule(String id, TripScheduleRequest request) {
        if (!scheduleRepository.existsById(id)) {
            throw new DataNotFoundException(List.of("Schedule not found"));
        }
        var newSchedule = locationClient.getTripSchedule(request, variableConfig.LOCATION_API_KEY).orElseThrow(
                () -> new DataNotFoundException(List.of("Locations not found"))
        );
        newSchedule.setId(id);
        scheduleRepository.save(newSchedule);
        return newSchedule;
    }
}
