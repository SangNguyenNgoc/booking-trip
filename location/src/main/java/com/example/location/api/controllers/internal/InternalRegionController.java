package com.example.location.api.controllers.internal;

import com.example.location.api.dtos.location.TripScheduleResponse;
import com.example.location.api.services.interfaces.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/regions")
@RequiredArgsConstructor
public class InternalRegionController {

    private final RegionService regionService;

    @GetMapping("/info")
    public ResponseEntity<TripScheduleResponse> getTripSchedule(
            @RequestParam(value = "from") String from,
            @RequestParam(value = "to") String to
    ) {
        return ResponseEntity.ok(regionService.getAllRegionsBySlug(from, to));
    }
}
