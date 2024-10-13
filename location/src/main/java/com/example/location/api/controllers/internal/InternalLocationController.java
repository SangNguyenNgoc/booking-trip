package com.example.location.api.controllers.internal;

import com.example.location.api.dtos.location.LocationName;
import com.example.location.api.dtos.location.TripScheduleRequest;
import com.example.location.api.dtos.location.TripScheduleResponse;
import com.example.location.api.services.interfaces.LocationService;
import com.example.location.utils.dtos.ListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/locations")
@RequiredArgsConstructor
public class InternalLocationController {

    private final LocationService locationService;

    @GetMapping("/schedule")
    public ResponseEntity<TripScheduleResponse> getTripSchedule(@RequestBody TripScheduleRequest request) {
        return ResponseEntity.ok(locationService.getTripSchedule(request));
    }

    @GetMapping("/names")
    public ResponseEntity<ListResponse<LocationName>> getAllLocationNames() {
        return ResponseEntity.ok(locationService.getLocationNames());
    }
}
