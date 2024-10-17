package org.tripservice.trip.api.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.tripservice.trip.api.documents.Trip;

public interface TripRepository extends MongoRepository<Trip, String> {
}
