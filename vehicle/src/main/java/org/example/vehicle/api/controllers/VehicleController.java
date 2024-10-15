package org.example.vehicle.api.controllers;

import lombok.RequiredArgsConstructor;
import org.example.vehicle.api.dtos.vehicle.VehicleCreate;
import org.example.vehicle.api.dtos.vehicle.VehicleDetail;
import org.example.vehicle.api.dtos.vehicle.VehicleInfo;
import org.example.vehicle.api.dtos.vtype.VehicleTypeDetail;
import org.example.vehicle.api.dtos.vtype.VehicleTypeInfo;
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

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<VehicleDetail> createVehicle(@RequestBody VehicleCreate vehicleCreate) {
        VehicleDetail vehicleDetail = vehicleService.createVehicle(vehicleCreate);
        return ResponseEntity.ok(vehicleDetail);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<PageResponse<VehicleInfo>> getVehicles(
            @RequestParam(value = "page", required = false) Integer pageNo,
            @RequestParam(value = "size", required = false) Integer pageSize
    ) {
        if(pageNo == null || pageSize == null) {
            return ResponseEntity.ok(vehicleService.getAllVehicles());
        }
        return ResponseEntity.ok(vehicleService.getAllVehicles(pageNo, pageSize));
    }


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


    @GetMapping("/{id}")
    public ResponseEntity<VehicleDetail> getVehicleDetailById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleDetailById(id));
    }

    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<VehicleDetail> toggleVehicleTypeById(@PathVariable(name = "id") Long vehicleId) {
        return ResponseEntity.ok(vehicleService.toggleVehicle(vehicleId));
    }

    @PutMapping("/{vehicleId}/trip/{tripId}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN','SCOPE_ROLE_EMPLOYEE')")
    @ResponseStatus(HttpStatus.OK)
    public void assignVehicleToTrip(
            @PathVariable(name = "vehicleId") Long vehicleId,
            @PathVariable(name = "tripId") String tripId
    ) {
        vehicleService.assignVehicleToTrip(vehicleId, tripId);
    }
}
