package org.example.vehicle.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.vehicle.api.dtos.assign.AssignScheduleCreate;
import org.example.vehicle.api.entities.AssignSchedule;
import org.example.vehicle.api.repositories.AssignScheduleRepository;
import org.example.vehicle.api.repositories.VehicleRepository;
import org.example.vehicle.api.services.interfaces.AssignScheduleService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DefaultAssignScheduleService implements AssignScheduleService {

    AssignScheduleRepository assignScheduleRepository;
    VehicleRepository vehicleRepository;

    @Override
    @KafkaListener(topics = "vehiclesAreAssigned")
    public void creatAssignSchedule(List<AssignScheduleCreate> assignScheduleCreates) {
        var vehicleIds = assignScheduleCreates.stream()
                .map(AssignScheduleCreate::getVehicleId)
                .toList();
        var vehicles = vehicleRepository.findAllById(vehicleIds);
        var assignSchedules = assignScheduleCreates.stream()
                .map(item -> AssignSchedule.builder()
                        .startDate(item.getStartDate())
                        .endDate(item.getEndDate())
                        .route(item.getRoute())
                        .vehicle(vehicles.stream().filter(v -> v.getId().equals(item.getVehicleId())).findFirst().orElse(null))
                        .build())
                .toList();
        assignScheduleRepository.saveAll(assignSchedules);
    }
}
