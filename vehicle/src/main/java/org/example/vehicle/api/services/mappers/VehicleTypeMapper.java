package org.example.vehicle.api.services.mappers;

import org.example.vehicle.api.dtos.vtype.VehicleTypeDetail;
import org.example.vehicle.api.dtos.vtype.VehicleTypeInfo;
import org.example.vehicle.api.entities.VehicleType;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VehicleTypeMapper {

    VehicleTypeInfo toInfo(VehicleType vehicleType);

    VehicleTypeDetail toDetail(VehicleType vehicleType);

}
