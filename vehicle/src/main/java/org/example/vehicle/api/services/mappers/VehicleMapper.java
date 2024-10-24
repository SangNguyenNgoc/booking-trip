package org.example.vehicle.api.services.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vehicle.api.dtos.vtype.VehicleTypeCreate;
import org.example.vehicle.api.dtos.vehicle.VehicleDetail;
import org.example.vehicle.api.dtos.vehicle.VehicleInfo;
import org.example.vehicle.api.entities.Vehicle;
import org.example.vehicle.api.entities.VehicleType;
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
            var belongToLocation = objectMapper.readValue(
                    vehicle.getBelongTo(),
                    VehicleInfo.LocationDto.class
            );
            vehicleInfo.setBelong(belongToLocation);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterMapping
    default void setLocationDto(@MappingTarget VehicleDetail vehicleDetail, Vehicle vehicle) {
        ObjectMapper objectMapper = new ObjectMapper();
        VehicleDetail.LocationDto locationDto = null;
        try {
            if (vehicle.getCurrentLocation() != null) {
                locationDto = objectMapper.readValue(vehicle.getCurrentLocation(), VehicleDetail.LocationDto.class);
                vehicleDetail.setNowAt(locationDto);
            } else {
                vehicleDetail.setNowAt(null);
            }
            var belongToLocation = objectMapper.readValue(
                    vehicle.getBelongTo(),
                    VehicleDetail.LocationDto.class
            );
            vehicleDetail.setBelong(belongToLocation);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    VehicleType toEntity(VehicleTypeCreate vehicleTypeCreate);

    @AfterMapping
    default void linkSeats(@MappingTarget VehicleType vehicleType) {
        vehicleType.getSeats().forEach(seat -> seat.setVehicleType(vehicleType));
    }

    VehicleTypeCreate toDto(VehicleType vehicleType);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    VehicleType partialUpdate(VehicleTypeCreate vehicleTypeCreate, @MappingTarget VehicleType vehicleType);
}
