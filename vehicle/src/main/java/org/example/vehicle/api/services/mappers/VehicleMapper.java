package org.example.vehicle.api.services.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vehicle.api.dtos.vehicle.VehicleCreate;
import org.example.vehicle.api.dtos.vehicle.VehicleDetail;
import org.example.vehicle.api.dtos.vehicle.VehicleInfo;
import org.example.vehicle.api.entities.Vehicle;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    VehicleDetail toDetail(Vehicle vehicle);

    VehicleInfo toInfo(Vehicle vehicle);

    @AfterMapping
    default void setLocationDto(@MappingTarget VehicleInfo vehicleInfo, Vehicle vehicle) {
        ObjectMapper objectMapper = new ObjectMapper();
        VehicleInfo.LocationDto locationDto = null;
        try {
            if (vehicle.getCurrentLocation() != null) {
                locationDto = objectMapper.readValue(vehicle.getCurrentLocation(), VehicleInfo.LocationDto.class);
                vehicleInfo.setNowAt(locationDto);
            } else {
                vehicleInfo.setNowAt(null);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    Vehicle toEntity(VehicleCreate vehicleCreate);

    VehicleCreate toDto(Vehicle vehicle);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Vehicle partialUpdate(VehicleCreate vehicleCreate, @MappingTarget Vehicle vehicle);
}
