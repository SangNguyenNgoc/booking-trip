package org.example.vehicle.api.services.interfaces;

import org.example.vehicle.api.dtos.vehicle.VehicleCreate;
import org.example.vehicle.api.dtos.vehicle.VehicleDetail;
import org.example.vehicle.api.dtos.vehicle.VehicleInfo;
import org.example.vehicle.utils.dtos.PageResponse;

import java.util.List;

public interface VehicleService {

    List<String> getAllLicensePlate();

    List<String> getLicensePlateByType(Long typeId);

    PageResponse<VehicleInfo> getAllVehicles();

    PageResponse<VehicleInfo> getAllVehicles(Integer pageNo, Integer pageSize);

    VehicleDetail getVehicleDetailById(Long id);

    VehicleDetail createVehicle(VehicleCreate vehicleCreate);

}
