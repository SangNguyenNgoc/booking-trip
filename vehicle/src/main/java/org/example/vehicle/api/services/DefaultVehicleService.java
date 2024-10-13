package org.example.vehicle.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.vehicle.api.dtos.vehicle.VehicleCreate;
import org.example.vehicle.api.dtos.vehicle.VehicleDetail;
import org.example.vehicle.api.dtos.vehicle.VehicleInfo;
import org.example.vehicle.api.entities.Vehicle;
import org.example.vehicle.api.entities.VehicleType;
import org.example.vehicle.api.entities.enums.VehicleStatus;
import org.example.vehicle.api.repositories.VehicleRepository;
import org.example.vehicle.api.repositories.VehicleTypeRepository;
import org.example.vehicle.api.services.interfaces.VehicleService;
import org.example.vehicle.api.services.mappers.VehicleMapper;
import org.example.vehicle.clients.LocationClient;
import org.example.vehicle.config.VariableConfig;
import org.example.vehicle.utils.dtos.ListResponse;
import org.example.vehicle.utils.dtos.PageResponse;
import org.example.vehicle.utils.exception.DataNotFoundException;
import org.example.vehicle.utils.services.ObjectsValidator;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DefaultVehicleService implements VehicleService {

    VehicleRepository vehicleRepository;
    VehicleTypeRepository vehicleTypeRepository;

    LocationClient locationClient;

    VehicleMapper vehicleMapper;
    ObjectsValidator<VehicleCreate> vehicleCreateValidator;

    VariableConfig variableConfig;

    @Override
    public List<String> getAllLicensePlate() {
        return vehicleRepository.getAllLicensePlates();
    }

    @Override
    public List<String> getLicensePlateByType(Long typeId) {
        return vehicleRepository.getAllLicensePlatesByType(typeId);
    }

    @Override
    public PageResponse<VehicleInfo> getAllVehicles() {
        var vehicles = vehicleRepository.findAll();
        var vehicleList = ListResponse.<VehicleInfo>builder()
                .size(vehicles.size())
                .data(vehicles.stream().map(vehicleMapper::toInfo).collect(Collectors.toList()))
                .build();
        return PageResponse.<VehicleInfo>builder()
                .totalPage(1)
                .currentPage(1)
                .data(vehicleList)
                .build();
    }

    @Override
    public PageResponse<VehicleInfo> getAllVehicles(Integer pageNo, Integer pageSize) {
        PageRequest page = PageRequest.of(pageNo, pageSize);
        var vehicles = vehicleRepository.findAll(page);
        var vehicleList = ListResponse.<VehicleInfo>builder()
                .size(vehicles.getSize())
                .data(vehicles.stream().map(vehicleMapper::toInfo).collect(Collectors.toList()))
                .build();
        return PageResponse.<VehicleInfo>builder()
                .totalPage(vehicles.getTotalPages())
                .currentPage(pageNo + 1)
                .data(vehicleList)
                .build();
    }

    @Override
    public VehicleDetail getVehicleDetailById(Long id) {
        var vehicle = vehicleRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException(List.of("Vehicle not found"))
        );
        return vehicleMapper.toDetail(vehicle);
    }

    @Override
    public VehicleDetail createVehicle(VehicleCreate vehicleCreate) {
        vehicleCreateValidator.validate(vehicleCreate);
        VehicleType type = vehicleTypeRepository.findById(vehicleCreate.getTypeId()).orElseThrow(
                () -> new DataNotFoundException(List.of("Vehicle type not found"))
        );
        var location = locationClient.getLocation(
                vehicleCreate.getCurrentLocationSlug(),
                variableConfig.LOCATION_API_KEY
        ).orElseThrow(
                () -> new DataNotFoundException(List.of("Location not found"))
        );
        Vehicle vehicle = Vehicle.builder()
                .licensePlate(vehicleCreate.getLicensePlate())
                .manufacturingDate(vehicleCreate.getManufacturingDate())
                .currentLocation(location)
                .type(type)
                .status(VehicleStatus.ARRIVED)
                .build();
        return vehicleMapper.toDetail(vehicleRepository.save(vehicle));
    }
}
