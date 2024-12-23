package org.tripservice.trip.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.tripservice.trip.api.documents.Schedule;
import org.tripservice.trip.api.dtos.location.RegionAndSchedule;
import org.tripservice.trip.api.dtos.location.RegionInfo;
import org.tripservice.trip.api.dtos.schedule.*;
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
import java.util.Map;
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

    Environment environment;

    KafkaTemplate<String, ScheduleStatistic> scheduleStatisticTemplate;

    @Override
    public ScheduleDetail createSchedule(ScheduleRequest request) {
        tripScheduleValidator.validate(request);
        var schedule = locationClient.getTripSchedule(request, variableConfig.LOCATION_API_KEY).orElseThrow(
                () -> new DataNotFoundException(List.of("Locations not found"))
        );
        double roundedUp = roundedUp10(schedule.getDuration());
        schedule.setDuration(roundedUp);
        for (var item : schedule.getPickUps()) {
            item.setDurationToLocation(roundedUp10(item.getDurationToLocation()));
        }
        for (var item : schedule.getTransits()) {
            item.setDurationToLocation(roundedUp10(item.getDurationToLocation()));
        }
        var vehicleType = vehicleTypeRepository.findById(request.getVehicleTypeId()).orElseThrow(
                () -> new DataNotFoundException(List.of("Vehicle types not found"))
        );
        schedule.setVehicleTypeId(vehicleType.getId());
        schedule.setVehicleTypeName(vehicleType.getName());
        schedule.setPrice(roundPrice(vehicleType.getPrice() * schedule.getDistance(), 10000));
        schedule.setBookedCount(0L);
        scheduleRepository.save(schedule);
        var scheduleStatistic = ScheduleStatistic.builder()
                .id(schedule.getId())
                .regionFrom(schedule.getRegionFrom().getName())
                .regionTo(schedule.getRegionTo().getName())
                .from(schedule.getFrom().getName())
                .to(schedule.getTo().getName())
                .price(schedule.getPrice())
                .build();
        scheduleStatisticTemplate.send("ScheduleCreated", scheduleStatistic);
        return scheduleMapper.toDetail(schedule);
    }


    @Override
    public ListResponse<RegionAndSchedule> getSchedulesByFromAndToGrouping(String from, String to) {
        List<Schedule> schedules;
        if (from == null || to == null) {
            schedules = scheduleRepository.findAll();
        } else {
            schedules = scheduleRepository.findByRegionFromAndRegionTo(from, to);
        }
        Map<RegionInfo, List<Schedule>> groupedSchedules = schedules.stream()
                .collect(Collectors.groupingBy(Schedule::getRegionFrom));
        List<RegionAndSchedule> result = new ArrayList<>();
        groupedSchedules.forEach((regionFrom, scheduleList) -> {
            var regionAndSchedule = RegionAndSchedule.builder()
                    .name(regionFrom.getName())
                    .slug(regionFrom.getSlug())
                    .nameWithType(regionFrom.getNameWithType())
                    .schedules(scheduleList.stream().map(scheduleMapper::toDto).collect(Collectors.toList()))
                    .build();
            result.add(regionAndSchedule);
        });
        return ListResponse.<RegionAndSchedule>builder()
                .size(result.size())
                .data(result)
                .build();
    }


    @Override
    public ListResponse<ScheduleResponse> getSchedulesByFromAndTo(String from, String to) {
        List<Schedule> schedules;

        if (from != null && to != null) {
            schedules = scheduleRepository.findByRegionFromAndRegionTo(from, to);
        } else if (from != null) {
            schedules = scheduleRepository.findByRegionFrom(from);
        } else {
            schedules = scheduleRepository.findAll();
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
    public List<RegionAndSchedule> getPopularSchedule() {
        var popularRegions = Binder.get(environment)
                .bind("region-popular", Bindable.listOf(String.class))
                .get();
        List<RegionAndSchedule> response = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 3);
        popularRegions.forEach(region -> {
            var schedules = scheduleRepository.findTop3ByRegionFromOrderByBookedCountDesc(region, pageable);
            var regionAndSchedule = RegionAndSchedule.builder()
                    .name(schedules.get(0).getRegionFrom().getName())
                    .nameWithType(schedules.get(0).getRegionFrom().getNameWithType())
                    .slug(region)
                    .schedules(schedules.stream().map(scheduleMapper::toDto).collect(Collectors.toList()))
                    .build();
            response.add(regionAndSchedule);
        });
        return response;
    }


    @Override
    public ScheduleDetail getSchedule(String id) {
        var schedule = scheduleRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException(List.of("Schedule not found"))
        );
        return scheduleMapper.toDetail(schedule);
    }


    public double roundedUp10(double input) {
        return Math.ceil(input / 10) * 10;
    }


    public Long roundPrice(double price, int roundTo) {
        return ((long) ((price + roundTo - 1) / roundTo) * roundTo);
    }


    @Override
    public ListResponse<ScheduleGroup> getSchedulesByFromAndToGroupByLocation(String from, String to) {
        var schedules = scheduleRepository.findByRegionFromAndRegionTo(from, to);
        var contrarySchedules = scheduleRepository.findByRegionFromAndRegionTo(to, from);
        List<ScheduleGroup> result = new ArrayList<>();
        schedules.forEach(schedule -> {
            var scheduleList = contrarySchedules.stream()
                    .filter(item -> {
                        boolean condition = item.getFrom().getSlug().equals(schedule.getTo().getSlug())
                                && item.getTo().getSlug().equals(schedule.getFrom().getSlug());
                        return condition;
                    })
                    .collect(Collectors.toList());
            scheduleList.forEach(item -> result.add(
                    ScheduleGroup.builder()
                            .schedule(scheduleMapper.toDetail(schedule))
                            .contrarySchedule(scheduleMapper.toDetail(item))
                            .build()
            ));
        });
        return ListResponse.<ScheduleGroup>builder()
                .size(result.size())
                .data(result)
                .build();
    }
}
