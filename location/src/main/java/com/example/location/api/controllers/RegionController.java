package com.example.location.api.controllers;

import com.example.location.api.dtos.region.RegionInfo;
import com.example.location.api.services.interfaces.RegionService;
import com.example.location.utils.dtos.ListResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @Operation(
            summary = "Get all regions.",
            description = "Get all regions."
    )
    @GetMapping
    public ResponseEntity<ListResponse<RegionInfo>> getAllRegions() {
        return ResponseEntity.ok(regionService.getAllRegions());
    }


    @Operation(
            summary = "Get region by slug.",
            description = "Get region by slug."
    )
    @GetMapping("/{slug}")
    public ResponseEntity<RegionInfo> getRegionBySlug(@PathVariable("slug") String slug) {
        return ResponseEntity.ok(regionService.getRegionBySlug(slug));
    }

}
