package org.example.vehicle.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.vehicle.api.dtos.vtype.VehicleTypeCreate;
import org.example.vehicle.api.dtos.vtype.VehicleTypeDetail;
import org.example.vehicle.api.dtos.vtype.VehicleTypeInfo;
import org.example.vehicle.api.dtos.vtype.VehicleTypeUpdate;
import org.example.vehicle.api.entities.Seat;
import org.example.vehicle.api.entities.VehicleType;
import org.example.vehicle.api.repositories.VehicleRepository;
import org.example.vehicle.api.repositories.VehicleTypeRepository;
import org.example.vehicle.api.services.interfaces.VehicleTypeService;
import org.example.vehicle.api.services.mappers.VehicleTypeMapper;
import org.example.vehicle.utils.dtos.ListResponse;
import org.example.vehicle.utils.exception.DataNotFoundException;
import org.example.vehicle.utils.services.ObjectsValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DefaultVehicleTypeService implements VehicleTypeService {

    VehicleTypeRepository vehicleTypeRepository;
    VehicleRepository vehicleRepository;

    VehicleTypeMapper vehicleTypeMapper;

    ObjectsValidator<VehicleTypeCreate> createValidator;
    ObjectsValidator<VehicleTypeUpdate> updateValidator;
    ObjectsValidator<VehicleTypeCreate.EmptySeat> emptySeatValidator;

    @Override
    public ListResponse<VehicleTypeInfo> getAllVehicleTypes() {
        var vehicleTypes = vehicleTypeRepository.findAll();
        return ListResponse.<VehicleTypeInfo>builder()
                .size(vehicleTypes.size())
                .data(vehicleTypes.stream().map(vehicleTypeMapper::toInfo).collect(Collectors.toList()))
                .build();
    }

    @Override
    public VehicleTypeDetail getVehicleTypeById(Long vehicleTypeId) {
        var vehicleType = vehicleTypeRepository.findByIdAndActiveTrue(vehicleTypeId).orElseThrow(
                () -> new DataNotFoundException(List.of("Vehicle type not found"))
        );
        return vehicleTypeMapper.toDetail(vehicleType);
    }

    @Override
    public ListResponse<VehicleTypeInfo> getAllVehicleTypeIsActive() {
        var vehicleTypes = vehicleTypeRepository.findByActiveTrue();
        return ListResponse.<VehicleTypeInfo>builder()
                .size(vehicleTypes.size())
                .data(vehicleTypes.stream().map(vehicleTypeMapper::toInfo).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public VehicleTypeDetail toggleVehicleType(Long vehicleTypeId) {
        var vehicleType = vehicleTypeRepository.findById(vehicleTypeId).orElseThrow(
                () -> new DataNotFoundException(List.of("Vehicle type not found"))
        );
        boolean isActive = vehicleType.getActive();
        vehicleType.setActive(!isActive);
        vehicleRepository.updateActiveByType(!isActive, vehicleType);
        return vehicleTypeMapper.toDetail(vehicleType);
    }

    @Override
    public VehicleTypeDetail createVehicleType(VehicleTypeCreate vehicleTypeCreate) {
        createValidator.validate(vehicleTypeCreate);
        vehicleTypeCreate.getEmptySeats().forEach(emptySeatValidator::validate);

        var vehicleType = VehicleType.builder()
                .name(vehicleTypeCreate.getName())
                .description(vehicleTypeCreate.getDescription())
                .numberOfFloors(vehicleTypeCreate.getNumberOfFloors())
                .numberOfRows(vehicleTypeCreate.getNumberOfRows())
                .seatsPerRow(vehicleTypeCreate.getSeatsPerRow())
                .numberOfSeats(vehicleTypeCreate.getNumberOfSeats())
                .active(true)
                .build();
        vehicleType.setSeats(createSeats(vehicleTypeCreate, vehicleType));
        var vehicleTypeSaved = vehicleTypeRepository.save(vehicleType);
        return vehicleTypeMapper.toDetail(vehicleTypeSaved);
    }


    @Override
    @Transactional
    public VehicleTypeDetail updateVehicleType(VehicleTypeUpdate vehicleTypeUpdate) {
        updateValidator.validate(vehicleTypeUpdate);
        var vehicleType = vehicleTypeRepository.findById(vehicleTypeUpdate.getId()).orElseThrow(
                () -> new DataNotFoundException(List.of("Vehicle type not found"))
        );
        var vehicleUpdated = vehicleTypeMapper.partialUpdate(vehicleTypeUpdate, vehicleType);
        return vehicleTypeMapper.toDetail(vehicleUpdated);
    }


    public Set<Seat> createSeats(VehicleTypeCreate vehicleTypeCreate, VehicleType vehicleType) {
        Set<Seat> seats = new HashSet<>();
        for (int floor = 1; floor <= vehicleTypeCreate.getNumberOfFloors(); floor++) {
            char floorLetter = (char) ('A' + floor - 1);
            int number = 1;
            for (int row = 1; row <= vehicleTypeCreate.getNumberOfRows(); row++) {
                for (int col = 1; col <= vehicleTypeCreate.getSeatsPerRow(); col++) {
                    String seatName = String.format("%c%02d", floorLetter, number);
                    int finalFloor = floor;
                    int finalRow = row;
                    int finalCol = col;
                    boolean isEmptySeat = vehicleTypeCreate.getEmptySeats().stream()
                            .anyMatch(emptySeat -> emptySeat.getFloorNo().equals(finalFloor)
                                    && emptySeat.getRowNo().equals(finalRow)
                                    && emptySeat.getColNo().equals(finalCol));
                    if (!isEmptySeat) {
                        seats.add(Seat.builder()
                                .name(seatName)
                                .colNo(finalCol)
                                .floorNo(finalFloor)
                                .rowNo(finalRow)
                                .vehicleType(vehicleType)
                                .build()
                        );
                        number++;
                    } else {
                        seats.add(Seat.builder()
                                .colNo(finalCol)
                                .floorNo(finalFloor)
                                .rowNo(finalRow)
                                .vehicleType(vehicleType)
                                .build()
                        );
                    }
                }
            }
        }
        return seats;
    }
}
