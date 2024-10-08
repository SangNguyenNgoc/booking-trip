package org.example.vehicle.api.services.interfaces;

import org.example.vehicle.api.dtos.vtype.VehicleTypeDetail;
import org.example.vehicle.api.dtos.vtype.VehicleTypeInfo;
import org.example.vehicle.utils.dtos.PageResponse;

public interface VehicleTypeService {

    PageResponse<VehicleTypeInfo> getAllVehicleTypes();

    PageResponse<VehicleTypeInfo> getAllVehicleTypes(Integer pageNo, Integer pageSize);

    VehicleTypeDetail getVehicleTypeById(Long vehicleTypeId);
}
