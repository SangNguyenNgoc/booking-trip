package org.example.vehicle.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.vehicle.api.dtos.trip.TripCompleted;
import org.example.vehicle.api.dtos.vehicle.VehicleCreate;
import org.example.vehicle.api.dtos.vehicle.VehicleDetail;
import org.example.vehicle.api.dtos.vehicle.VehicleInfo;
import org.example.vehicle.api.entities.Vehicle;
import org.example.vehicle.api.entities.VehicleType;
import org.example.vehicle.api.entities.enums.VehicleStatus;
import org.example.vehicle.api.repositories.VehicleRepository;
import org.example.vehicle.api.repositories.VehicleTypeRepository;
import org.example.vehicle.api.repositories.specifications.VehicleSpecifications;
import org.example.vehicle.api.services.interfaces.VehicleService;
import org.example.vehicle.api.services.mappers.VehicleMapper;
import org.example.vehicle.clients.LocationClient;
import org.example.vehicle.config.VariableConfig;
import org.example.vehicle.utils.dtos.ListResponse;
import org.example.vehicle.utils.dtos.PageResponse;
import org.example.vehicle.utils.exception.DataNotFoundException;
import org.example.vehicle.utils.exception.InputInvalidException;
import org.example.vehicle.utils.services.ObjectsValidator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DefaultVehicleService implements VehicleService {

    VehicleRepository vehicleRepository;
    VehicleTypeRepository vehicleTypeRepository;
    VehicleSpecifications vehicleSpecifications;

    LocationClient locationClient;

    VehicleMapper vehicleMapper;
    ObjectsValidator<VehicleCreate> vehicleCreateValidator;

    VariableConfig variableConfig;

    @Override
    public List<String> getAllLicensePlate() {
        return vehicleRepository.findAllLicensePlates();
    }


    @Override
    public List<String> getLicensePlateByType(Long typeId) {
        return vehicleRepository.findAllLicensePlatesByType(typeId);
    }


    @Override
    public PageResponse<VehicleInfo> getAllVehiclesByCondition(String belongTo, Long typeId, String status) {
        List<Supplier<PageResponse<VehicleInfo>>> conditions = Arrays.asList(
                () -> (belongTo != null && typeId != null && status != null) ?
                        getAllVehiclesByBelongToAndTypeAndStatus(belongTo, typeId, status) : null,
                () -> (typeId != null && belongTo != null) ?
                        getAllVehiclesByBelongToAndType(belongTo, typeId) : null,
                () -> (belongTo != null) ?
                        getAllVehiclesByBelongTo(belongTo) : null,
                this::getAllVehicles
        );

        return conditions.stream()
                .map(Supplier::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(List.of("No matching condition")));
    }


    @Override
    public PageResponse<VehicleInfo> getAllVehiclesByCondition(
            String belongTo, Long typeId, String status, Integer pageNo, Integer pageSize
    ) {
        List<Supplier<PageResponse<VehicleInfo>>> conditions = Arrays.asList(
                () -> (typeId != null && status != null && belongTo != null) ?
                        getAllVehiclesByBelongToAndTypeAndStatus(pageNo, pageSize, belongTo, typeId, status) : null,
                () -> (typeId != null && belongTo != null) ?
                        getAllVehiclesByBelongToAndType(pageNo, pageSize, belongTo, typeId) : null,
                () -> (belongTo != null) ?
                        getAllVehiclesByBelongTo(pageNo, pageSize, belongTo) : null,
                this::getAllVehicles
        );

        return conditions.stream()
                .map(Supplier::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(List.of("No matching condition")));
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
    public PageResponse<VehicleInfo> getAllVehiclesByBelongTo(String belongTo) {
        var vehicles = vehicleRepository.findAllByBelongTo(belongTo);
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
    public PageResponse<VehicleInfo> getAllVehiclesByBelongTo(Integer pageNo, Integer pageSize, String belongTo) {
        PageRequest page = PageRequest.of(pageNo, pageSize);
        var vehicles = vehicleRepository.findAllByBelongTo(belongTo, page);
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
    public PageResponse<VehicleInfo> getAllVehiclesByBelongToAndType(String belongTo, Long typeId) {
        Specification<Vehicle> spec = Specification.where(vehicleSpecifications.beLongToAt(belongTo))
                .and(vehicleSpecifications.hasTypeId(typeId));
        var vehicles = vehicleRepository.findAll(spec);
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
    public PageResponse<VehicleInfo> getAllVehiclesByBelongToAndType(Integer pageNo, Integer pageSize, String belongTo, Long typeId) {
        Specification<Vehicle> spec = Specification.where(vehicleSpecifications.beLongToAt(belongTo))
                .and(vehicleSpecifications.hasTypeId(typeId));
        PageRequest page = PageRequest.of(pageNo, pageSize);
        var vehicles = vehicleRepository.findAll(spec, page);
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
    public PageResponse<VehicleInfo> getAllVehiclesByBelongToAndTypeAndStatus(String belongTo, Long typeId, String status) {
        Specification<Vehicle> spec = Specification.where(vehicleSpecifications.beLongToAt(belongTo))
                .and(vehicleSpecifications.hasTypeId(typeId))
                .and(vehicleSpecifications.hasStatus(VehicleStatus.valueOf(status)));
        var vehicles = vehicleRepository.findAll(spec);
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
    public PageResponse<VehicleInfo> getAllVehiclesByBelongToAndTypeAndStatus(Integer pageNo, Integer pageSize, String belongTo, Long typeId, String status) {
        PageRequest page = PageRequest.of(pageNo, pageSize);
        Specification<Vehicle> spec = Specification.where(vehicleSpecifications.beLongToAt(belongTo))
                .and(vehicleSpecifications.hasTypeId(typeId))
                .and(vehicleSpecifications.hasStatus(VehicleStatus.valueOf(status)));
        var vehicles = vehicleRepository.findAll(spec, page);
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
        if (vehicleRepository.existsByLicensePlate(vehicleCreate.getLicensePlate())) {
            throw new InputInvalidException(List.of("License plate already exist"));
        }
        VehicleType type = vehicleTypeRepository.findById(vehicleCreate.getTypeId()).orElseThrow(
                () -> new DataNotFoundException(List.of("Vehicle type not found"))
        );
        var locationOptional = vehicleRepository.findFirstByCurrentLocation(vehicleCreate.getCurrentLocationSlug());
        var location = locationOptional.orElseGet(
                () -> locationClient.getLocation(
                        vehicleCreate.getCurrentLocationSlug(),
                        variableConfig.LOCATION_API_KEY
                ).orElseThrow(
                        () -> new DataNotFoundException(List.of("Location not found"))
                )
        );
        Vehicle vehicle = Vehicle.builder()
                .licensePlate(vehicleCreate.getLicensePlate())
                .manufacturingDate(vehicleCreate.getManufacturingDate())
                .currentLocation(location)
                .belongTo(location)
                .type(type)
                .status(VehicleStatus.ARRIVED)
                .active(true)
                .build();
        return vehicleMapper.toDetail(vehicleRepository.save(vehicle));
    }


    @Override
    @Transactional
    public VehicleDetail toggleVehicle(Long id) {
        var vehicle = vehicleRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException(List.of("Vehicle not found"))
        );
        var isActive = vehicle.getActive();
        vehicle.setActive(!isActive);
        return vehicleMapper.toDetail(vehicle);
    }


    @Override
    //Consumer kafka
    public void handleTripCompletedEvent(TripCompleted tripCompleted) {
        var locationOptional = vehicleRepository.findFirstByCurrentLocation(tripCompleted.getLocationSlug());
        var location = locationOptional.orElseGet(
                () -> locationClient.getLocation(
                        tripCompleted.getLocationSlug(),
                        variableConfig.LOCATION_API_KEY
                ).orElseThrow(
                        () -> new DataNotFoundException(List.of("Location not found"))
                )
        );
        var vehicle = vehicleRepository.findByIdAndActiveTrue(tripCompleted.getVehicleId()).orElseThrow(
                () -> new DataNotFoundException(List.of("Vehicle not found"))
        );
        vehicle.setStatus(VehicleStatus.ARRIVED);
        vehicle.setCurrentLocation(location);
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(tripCompleted.getArrivalTime()),
                ZoneId.of("Asia/Ho_Chi_Minh")
        );
        vehicle.setLastArrivalAt(dateTime);
        vehicleRepository.save(vehicle);
    }


    @Override
    //Consumer kafka
    public void handleTripDepartureEvent(Long vehicleId) {
        var vehicle = vehicleRepository.findByIdAndActiveTrue(vehicleId).orElseThrow(
                () -> new DataNotFoundException(List.of("Vehicle not found"))
        );
        vehicle.setStatus(VehicleStatus.ON_ROUTE);
        vehicle.setCurrentLocation(null);
        vehicleRepository.save(vehicle);
    }

}
