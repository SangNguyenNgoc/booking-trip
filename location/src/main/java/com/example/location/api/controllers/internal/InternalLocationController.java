package com.example.location.api.controllers.internal;

import com.example.location.api.dtos.location.LocationName;
import com.example.location.api.dtos.location.TripScheduleRequest;
import com.example.location.api.dtos.location.TripScheduleResponse;
import com.example.location.api.services.interfaces.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/locations")
@RequiredArgsConstructor
public class InternalLocationController {

    private final LocationService locationService;

    @PostMapping("/schedule")
    public ResponseEntity<TripScheduleResponse> getTripSchedule(@RequestBody TripScheduleRequest request) {
        return ResponseEntity.ok(locationService.getTripSchedule(request));
    }

    @GetMapping("/names/{slug}")
    public ResponseEntity<LocationName> getLocationNameBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(locationService.getLocationNameBySlug(slug));
    }
}
