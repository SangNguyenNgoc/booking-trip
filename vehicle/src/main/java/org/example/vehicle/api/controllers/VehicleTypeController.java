package org.example.vehicle.api.controllers;

import lombok.RequiredArgsConstructor;
import org.example.vehicle.api.dtos.vtype.VehicleTypeDetail;
import org.example.vehicle.api.dtos.vtype.VehicleTypeInfo;
import org.example.vehicle.api.services.interfaces.VehicleTypeService;
import org.example.vehicle.utils.dtos.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vtypes")
@RequiredArgsConstructor
public class VehicleTypeController {

    private final VehicleTypeService vehicleTypeService;

    @GetMapping
    public ResponseEntity<PageResponse<VehicleTypeInfo>> getVehicleTypes(
            @RequestParam(value = "page", required = false) Integer pageNo,
            @RequestParam(value = "size", required = false) Integer pageSize
    ) {
        if(pageNo == null || pageSize == null) {
            return ResponseEntity.ok(vehicleTypeService.getAllVehicleTypes());
        }
        return ResponseEntity.ok(vehicleTypeService.getAllVehicleTypes(pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleTypeDetail> getVehicleTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleTypeService.getVehicleTypeById(id));
    }
}
