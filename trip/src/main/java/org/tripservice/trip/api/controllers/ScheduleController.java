package org.tripservice.trip.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tripservice.trip.api.documents.Schedule;
import org.tripservice.trip.api.dtos.schedule.TripScheduleRequest;
import org.tripservice.trip.api.services.interfaces.ScheduleService;

import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@RequestBody TripScheduleRequest schedule) {
        return ResponseEntity.ok(scheduleService.createSchedule(schedule));
    }

    @GetMapping
    public ResponseEntity<List<Schedule>> getSchedules(
            @RequestParam(value = "from") String from,
            @RequestParam(value = "to") String to
    ) {
        return ResponseEntity.ok(scheduleService.getSchedulesByFromAndTo(from, to));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(
            @RequestBody TripScheduleRequest schedule,
            @PathVariable String id
    ) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, schedule));
    }
}
