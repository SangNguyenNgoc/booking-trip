package org.example.vehicle.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.vehicle.api.dtos.vehicle.VehicleCreate;
import org.example.vehicle.api.dtos.vehicle.VehicleDetail;
import org.example.vehicle.api.dtos.vehicle.VehicleInfo;
import org.example.vehicle.api.services.interfaces.VehicleService;
import org.example.vehicle.utils.dtos.PageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;


    @Operation(
            summary = "Create vehicle.",
            description = "Create vehicle, require 'ROLE_ADMIN'.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<VehicleDetail> createVehicle(@RequestBody VehicleCreate vehicleCreate) {
        VehicleDetail vehicleDetail = vehicleService.createVehicle(vehicleCreate);
        return ResponseEntity.ok(vehicleDetail);
    }


    @Operation(
            summary = "Get vehicles information.",
            description = "Get vehicles information, require 'ROLE_ADMIN'.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<PageResponse<VehicleInfo>> getVehicles(
            @RequestParam(value = "type", required = false) Long typeId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "page", required = false) Integer pageNo,
            @RequestParam(value = "size", required = false) Integer pageSize
    ) {
        if(pageNo == null || pageSize == null) {
            return ResponseEntity.ok(vehicleService.getAllVehiclesByCondition(typeId, status, location));
        }
        return ResponseEntity.ok(vehicleService.getAllVehiclesByCondition(typeId, status, location, pageNo, pageSize));
    }


    @Operation(
            summary = "Get vehicles licenses.",
            description = "Get vehicles licenses, require 'ROLE_ADMIN'.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/licenses")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<List<String>> getVehicleLicenses(
            @RequestParam(value = "type", required = false) Long typeId
    ) {
        if(typeId == null) {
            return ResponseEntity.ok(vehicleService.getAllLicensePlate());
        }
        return ResponseEntity.ok(vehicleService.getLicensePlateByType(typeId));
    }


    @Operation(
            summary = "Get detail information of vehicle by id.",
            description = "Get detail information of a vehicle by id, require 'ROLE_ADMIN'.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<VehicleDetail> getVehicleDetailById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleDetailById(id));
    }


    @Operation(
            summary = "Change active for vehicle by id.",
            description = "Change active for vehicle by id, require 'ROLE_ADMIN'.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<VehicleDetail> toggleVehicleTypeById(@PathVariable(name = "id") Long vehicleId) {
        return ResponseEntity.ok(vehicleService.toggleVehicle(vehicleId));
    }

}
