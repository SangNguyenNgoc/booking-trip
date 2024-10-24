package org.tripservice.trip.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.tripservice.trip.api.documents.VehicleType;
import org.tripservice.trip.api.repositories.VehicleTypeRepository;
import org.tripservice.trip.api.services.interfaces.VehicleTypeService;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DefaultVehicleTypeService implements VehicleTypeService {

    VehicleTypeRepository vehicleTypeRepository;

    @Override
    @KafkaListener(topics = "vehicleTypeIsCreated")
    public void createVehicleType(VehicleType vehicleType) {
        vehicleTypeRepository.save(vehicleType);
    }
}
