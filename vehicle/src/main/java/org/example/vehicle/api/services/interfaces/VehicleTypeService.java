package org.example.vehicle.api.services.interfaces;

import org.example.vehicle.api.dtos.vtype.VehicleTypeCreate;
import org.example.vehicle.api.dtos.vtype.VehicleTypeDetail;
import org.example.vehicle.api.dtos.vtype.VehicleTypeInfo;
import org.example.vehicle.api.dtos.vtype.VehicleTypeUpdate;
import org.example.vehicle.utils.dtos.ListResponse;
import org.example.vehicle.utils.dtos.PageResponse;

public interface VehicleTypeService {

    ListResponse<VehicleTypeInfo> getAllVehicleTypes();

    VehicleTypeDetail getVehicleTypeById(Long vehicleTypeId);

    ListResponse<VehicleTypeInfo> getAllVehicleTypeIsActive();

    VehicleTypeDetail toggleVehicleType(Long vehicleTypeId);

    VehicleTypeDetail createVehicleType(VehicleTypeCreate vehicleTypeCreate);

    VehicleTypeDetail updateVehicleType(VehicleTypeUpdate vehicleTypeUpdate);

}
