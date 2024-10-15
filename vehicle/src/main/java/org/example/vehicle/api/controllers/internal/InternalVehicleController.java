package org.example.vehicle.api.controllers.internal;

import lombok.RequiredArgsConstructor;
import org.example.vehicle.api.dtos.vtype.VehicleTypeDetail;
import org.example.vehicle.api.services.interfaces.VehicleTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalVehicleController {

    private final VehicleTypeService vehicleTypeService;

    @GetMapping("/vtypes/{id}")
    public ResponseEntity<VehicleTypeDetail> getVehicleTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleTypeService.getVehicleTypeById(id));
    }
}
