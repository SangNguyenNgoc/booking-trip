package org.tripservice.trip.api.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.tripservice.trip.api.documents.VehicleType;

public interface VehicleTypeRepository extends MongoRepository<VehicleType, Long> {
}
