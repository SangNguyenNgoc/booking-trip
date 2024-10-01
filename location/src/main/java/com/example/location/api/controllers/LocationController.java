package com.example.location.api.controllers;

import com.example.location.api.dtos.location.LocationCreate;
import com.example.location.api.dtos.location.LocationInfo;
import com.example.location.api.dtos.location.LocationName;
import com.example.location.api.dtos.location.LocationUpdate;
import com.example.location.api.services.interfaces.LocationService;
import com.example.location.utils.dtos.ListResponse;
import com.example.location.utils.dtos.PageResponse;
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
    public ResponseEntity<PageResponse<LocationInfo>> getAllLocations(
            @RequestParam(value = "page") Integer pageNo,
            @RequestParam(value = "size") Integer pageSize
    ) {
        return ResponseEntity.ok(locationService.getALlLocations(pageNo - 1, pageSize));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<LocationInfo> getLocationBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(locationService.getLocationBySlug(slug));
    }

    @GetMapping("/region/{regionId}")
    public ResponseEntity<PageResponse<LocationInfo>> getLocationByRegion(
            @PathVariable String regionId,
            @RequestParam(value = "page") Integer pageNo,
            @RequestParam(value = "size") Integer pageSize
    ) {
        return ResponseEntity.ok(locationService.getLocationByRegion(regionId, pageNo - 1, pageSize));
    }

    @GetMapping("/names")
    public ResponseEntity<ListResponse<LocationName>> getAllLocationNames() {
        return ResponseEntity.ok(locationService.getLocationNames());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<LocationInfo> updateLocation(@RequestBody LocationUpdate locationUpdate) {
        return ResponseEntity.ok(locationService.updateLocation(locationUpdate));
    }

    @PutMapping("/{locationId}/region")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<LocationInfo> updateLocation(
            @PathVariable String locationId,
            @RequestParam(value = "region") String regionSlug
    ) {
        return ResponseEntity.ok(locationService.updateRegionInLocation(locationId, regionSlug));
    }

    @PutMapping("/{locationId}/active")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<Void> toggleActiveLocation(
            @PathVariable String locationId
    ) {
        locationService.toggleActiveLocation(locationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
