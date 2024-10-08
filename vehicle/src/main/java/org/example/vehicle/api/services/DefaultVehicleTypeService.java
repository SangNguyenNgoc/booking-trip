package org.example.vehicle.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.vehicle.api.dtos.vtype.VehicleTypeDetail;
import org.example.vehicle.api.dtos.vtype.VehicleTypeInfo;
import org.example.vehicle.api.repositories.VehicleTypeRepository;
import org.example.vehicle.api.services.interfaces.VehicleTypeService;
import org.example.vehicle.api.services.mappers.VehicleTypeMapper;
import org.example.vehicle.utils.dtos.ListResponse;
import org.example.vehicle.utils.dtos.PageResponse;
import org.example.vehicle.utils.exception.DataNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DefaultVehicleTypeService implements VehicleTypeService {
    VehicleTypeRepository vehicleTypeRepository;
    VehicleTypeMapper vehicleTypeMapper;

    @Override
    public PageResponse<VehicleTypeInfo> getAllVehicleTypes() {
        var vehicleTypes = vehicleTypeRepository.findAll();
        var response = ListResponse.<VehicleTypeInfo>builder()
                .size(vehicleTypes.size())
                .data(vehicleTypes.stream().map(vehicleTypeMapper::toInfo).collect(Collectors.toList()))
                .build();
        return PageResponse.<VehicleTypeInfo>builder()
                .currentPage(1)
                .totalPage(1)
                .data(response)
                .build();
    }

    @Override
    public PageResponse<VehicleTypeInfo> getAllVehicleTypes(Integer pageNo, Integer pageSize) {
        PageRequest page = PageRequest.of(pageNo, pageSize);
        var vehicleTypes = vehicleTypeRepository.findAll(page);
        var response = ListResponse.<VehicleTypeInfo>builder()
                .size(vehicleTypes.getSize())
                .data(vehicleTypes.getContent().stream().map(vehicleTypeMapper::toInfo).collect(Collectors.toList()))
                .build();
        return PageResponse.<VehicleTypeInfo>builder()
                .currentPage(pageNo + 1)
                .totalPage(vehicleTypes.getTotalPages())
                .data(response)
                .build();
    }

    @Override
    public VehicleTypeDetail getVehicleTypeById(Long vehicleTypeId) {
        var vehicleType = vehicleTypeRepository.findById(vehicleTypeId).orElseThrow(
                () -> new DataNotFoundException(List.of("Vehicle type not found"))
        );
        return vehicleTypeMapper.toDetail(vehicleType);
    }

}
