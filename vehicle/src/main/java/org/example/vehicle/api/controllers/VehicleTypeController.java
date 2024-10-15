package org.example.vehicle.api.controllers;

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

    @GetMapping
    public ResponseEntity<ListResponse<VehicleTypeInfo>> getAllVehicleTypes() {
        return ResponseEntity.ok(vehicleTypeService.getAllVehicleTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleTypeDetail> getVehicleTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleTypeService.getVehicleTypeById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<ListResponse<VehicleTypeInfo>> getAllVehicleTypesIsActive() {
        return ResponseEntity.ok(vehicleTypeService.getAllVehicleTypeIsActive());
    }

    @PutMapping("{id}/toggle")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<VehicleTypeDetail> toggleVehicleTypeById(@PathVariable(name = "id") Long typeId) {
        return ResponseEntity.ok(vehicleTypeService.toggleVehicleType(typeId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<VehicleTypeDetail> createVehicleType(@RequestBody VehicleTypeCreate vehicleTypeCreate) {
        return ResponseEntity.ok(vehicleTypeService.createVehicleType(vehicleTypeCreate));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<VehicleTypeDetail> createVehicleType(@RequestBody VehicleTypeUpdate vehicleTypeUpdate) {
        return ResponseEntity.ok(vehicleTypeService.updateVehicleType(vehicleTypeUpdate));
    }
}
