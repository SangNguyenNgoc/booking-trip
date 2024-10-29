package org.tripservice.trip.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tripservice.trip.api.dtos.schedule.ScheduleResponse;
import org.tripservice.trip.api.dtos.trip.TripCreate;
import org.tripservice.trip.api.dtos.trip.TripDetail;
import org.tripservice.trip.api.dtos.trip.TripInfo;
import org.tripservice.trip.api.services.interfaces.TripService;
import org.tripservice.trip.utils.dtos.ListResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @Operation(
            summary = "Create trips.",
            description = "Create trips, require 'ROLE_ADMIN'.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<List<TripInfo>> createTrip(@RequestBody TripCreate tripCreate) {
        return ResponseEntity.ok(tripService.createTrip(tripCreate));
    }


    @Operation(
            summary = "Get trips by from, to, date.",
            description = "Get trips by from, to, date."
    )
    @GetMapping
    public ResponseEntity<ListResponse<ScheduleResponse>> getTripsByFromAndToInDate(
            @RequestParam(value = "from") String from,
            @RequestParam(value = "to") String to,
            @RequestParam(value = "fromDate") LocalDate fromDate,
            @RequestParam(value = "ticketCount") Integer ticketCount,
            @RequestParam(value = "timeInDay", required = false) String timeInDay,
            @RequestParam(value = "vehicleType", required = false) Long vehicleType
    ) {
        return ResponseEntity.ok(tripService.getSchedulesIncludeTripsByFromAndTo(
                from, to, fromDate,
                ticketCount, timeInDay, vehicleType
        ));
    }


    @Operation(
            summary = "Get trip detail.",
            description = "Get trip detail."
    )
    @GetMapping("/{id}")
    public ResponseEntity<TripDetail> getTripsByFromAndToInDate(
            @PathVariable("id") String tripId
    ) {
        return ResponseEntity.ok(tripService.getTripDetail(tripId));
    }
}
