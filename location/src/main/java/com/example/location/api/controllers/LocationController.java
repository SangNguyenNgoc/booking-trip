package com.example.location.api.controllers;

import com.example.location.api.dtos.location.LocationCreate;
import com.example.location.api.dtos.location.LocationInfo;
import com.example.location.api.dtos.location.LocationName;
import com.example.location.api.services.interfaces.LocationService;
import com.example.location.utils.dtos.ListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<LocationInfo> createLocation(@RequestBody LocationCreate locationCreate) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(locationService.createLocation(locationCreate));
    }

    @GetMapping
    public ResponseEntity<ListResponse<LocationInfo>> getAllLocations() {
        return ResponseEntity.ok(locationService.getALlLocations());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<LocationInfo> getLocationBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(locationService.getLocationBySlug(slug));
    }

    @GetMapping("/names")
    public ResponseEntity<ListResponse<LocationName>> getAllLocationNames() {
        return ResponseEntity.ok(locationService.getLocationNames());
    }
}
