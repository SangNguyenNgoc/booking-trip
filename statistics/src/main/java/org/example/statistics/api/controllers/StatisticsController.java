package org.example.statistics.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.statistics.api.dtos.GeneralStatistics;
import org.example.statistics.api.dtos.ScheduleStatistics;
import org.example.statistics.api.dtos.StatisticsRequest;
import org.example.statistics.api.dtos.TripStatistics;
import org.example.statistics.api.services.interfaces.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @Operation(
            summary = "Get general statistics",
            description = "This endpoint allows admin get general statistics",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping("")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<GeneralStatistics> getGeneralStatistics(@RequestBody StatisticsRequest statisticsRequest){
        return ResponseEntity.ok(statisticsService.getGeneralStatistics(statisticsRequest));
    }

    @Operation(
            summary = "Get trip statistics",
            description = "This endpoint allows admin get trip statistics",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/trips/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<TripStatistics> getTripStatistics(@PathVariable("id") String id){
        return ResponseEntity.ok(statisticsService.getTripStatistics(id));
    }

    @Operation(
            summary = "Get schedule statistics",
            description = "This endpoint allows admin get schedule statistics",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/schedules/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<ScheduleStatistics> getScheduleStatistics(
            @PathVariable("id") String id,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
            ){
        return ResponseEntity.ok(statisticsService.getScheduleStatistics(id, from, to));
    }
}
