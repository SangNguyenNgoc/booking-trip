package org.tripservice.trip.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tripservice.trip.api.dtos.location.RegionAndSchedule;
import org.tripservice.trip.api.dtos.schedule.ScheduleDetail;
import org.tripservice.trip.api.dtos.schedule.ScheduleRequest;
import org.tripservice.trip.api.dtos.schedule.ScheduleResponse;
import org.tripservice.trip.api.services.interfaces.ScheduleService;
import org.tripservice.trip.utils.dtos.ListResponse;

import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(
            summary = "Create schedule.",
            description = "Create schedule, require 'ROLE_ADMIN'.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<ScheduleDetail> createSchedule(@RequestBody ScheduleRequest schedule) {
        return ResponseEntity.ok(scheduleService.createSchedule(schedule));
    }


    @Operation(
            summary = "Get schedule by from, to or else, return all schedules grouping by region.",
            description = "Get schedule by from, to or else, return all schedules grouping by region."
    )
    @GetMapping("/group")
    public ResponseEntity<ListResponse<RegionAndSchedule>> getSchedulesGroupingByRegion(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to
    ) {
        return ResponseEntity.ok(scheduleService.getSchedulesByFromAndToGrouping(from, to));
    }


    @Operation(
            summary = "Get schedule by from, to or else, return all schedules.",
            description = "Get schedule by from, to or else, return all schedules. require 'ROLE_ADMIN'.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<ListResponse<ScheduleResponse>> getSchedules(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to
    ) {
        return ResponseEntity.ok(scheduleService.getSchedulesByFromAndTo(from, to));
    }


    @Operation(
            summary = "Get detail schedule.",
            description = "Get detail schedule, require 'ROLE_ADMIN'.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<ScheduleDetail> updateSchedule(@PathVariable String id) {
        return ResponseEntity.ok(scheduleService.getSchedule(id));
    }


    @Operation(
            summary = "Update schedule.",
            description = "Update schedule, require 'ROLE_ADMIN'.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<ScheduleDetail> updateSchedule(
            @RequestBody ScheduleRequest schedule,
            @PathVariable String id
    ) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, schedule));
    }


    @Operation(
            summary = "Get popular schedules.",
            description = "Get popular schedule."
    )
    @GetMapping("/popular")
    public ResponseEntity<List<RegionAndSchedule>> getPopularSchedules() {
        return ResponseEntity.ok(scheduleService.getPopularSchedule());
    }

}
