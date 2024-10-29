package org.example.vehicle.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.vehicle.api.dtos.vtype.VehicleTypeCreate;
import org.example.vehicle.api.dtos.vtype.VehicleTypeDetail;
import org.example.vehicle.api.dtos.vtype.VehicleTypeInfo;
import org.example.vehicle.api.dtos.vtype.VehicleTypeUpdate;
import org.example.vehicle.api.services.interfaces.VehicleTypeService;
import org.example.vehicle.utils.dtos.ListResponse;
import org.example.vehicle.utils.dtos.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vtypes")
@RequiredArgsConstructor
public class VehicleTypeController {

    private final VehicleTypeService vehicleTypeService;


    @Operation(
            summary = "Get all vehicle type information.",
            description = "Get all vehicle type information."
    )
    @GetMapping
    public ResponseEntity<ListResponse<VehicleTypeInfo>> getAllVehicleTypes() {
        return ResponseEntity.ok(vehicleTypeService.getAllVehicleTypes());
    }


    @Operation(
            summary = "Get detail information of vehicle type by id.",
            description = "Get detail information of vehicle type by id, require 'ROLE_ADMIN'.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<VehicleTypeDetail> getVehicleTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleTypeService.getVehicleTypeById(id));
    }


    @Operation(
            summary = "Get all vehicle type are active.",
            description = "Get all vehicle type are active, require 'ROLE_ADMIN'.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/active")
    public ResponseEntity<ListResponse<VehicleTypeInfo>> getAllVehicleTypesIsActive() {
        return ResponseEntity.ok(vehicleTypeService.getAllVehicleTypeIsActive());
    }


    @Operation(
            summary = "Toggle active vehicle type by id.",
            description = "Toggle active vehicle type by id, require 'ROLE_ADMIN'.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PutMapping("{id}/toggle")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<VehicleTypeDetail> toggleVehicleTypeById(@PathVariable(name = "id") Long typeId) {
        return ResponseEntity.ok(vehicleTypeService.toggleVehicleType(typeId));
    }


    @Operation(
            summary = "Create vehicle type.",
            description = "Create vehicle type, require 'ROLE_ADMIN'.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<VehicleTypeDetail> createVehicleType(@RequestBody VehicleTypeCreate vehicleTypeCreate) {
        return ResponseEntity.ok(vehicleTypeService.createVehicleType(vehicleTypeCreate));
    }


    @Operation(
            summary = "Update vehicle type.",
            description = "Update vehicle type, require 'ROLE_ADMIN'.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<VehicleTypeDetail> updateVehicleType(@RequestBody VehicleTypeUpdate vehicleTypeUpdate) {
        return ResponseEntity.ok(vehicleTypeService.updateVehicleType(vehicleTypeUpdate));
    }
}
