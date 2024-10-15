package org.example.vehicle.api.services.mappers;

import org.example.vehicle.api.dtos.vtype.VehicleTypeDetail;
import org.example.vehicle.api.dtos.vtype.VehicleTypeInfo;
import org.example.vehicle.api.dtos.vtype.VehicleTypeUpdate;
import org.example.vehicle.api.entities.VehicleType;
import org.mapstruct.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface VehicleTypeMapper {

    VehicleTypeInfo toInfo(VehicleType vehicleType);

    VehicleTypeDetail toDetail(VehicleType vehicleType);

    @AfterMapping
    default void sortSeats(@MappingTarget VehicleTypeDetail vehicleTypeDetail) {
        List<VehicleTypeDetail.SeatDto> seats = vehicleTypeDetail.getSeats().stream().sorted(
                Comparator.comparing(VehicleTypeDetail.SeatDto::getFloorNo)
                        .thenComparing(VehicleTypeDetail.SeatDto::getRowNo)
                        .thenComparing(VehicleTypeDetail.SeatDto::getColNo)
        ).toList();
        vehicleTypeDetail.setSeats(seats);
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    VehicleType partialUpdate(VehicleTypeUpdate vehicleTypeUpdate, @MappingTarget VehicleType vehicleType);
}
