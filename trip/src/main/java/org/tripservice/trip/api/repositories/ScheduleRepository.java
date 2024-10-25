package org.tripservice.trip.api.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.tripservice.trip.api.documents.Schedule;
import org.tripservice.trip.api.documents.VehicleType;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends MongoRepository<Schedule, String> {

    @Query("{'from.slug': ?0, 'to.slug': ?1}")
    List<Schedule> findByFromAndTo(String from, String to);

    Optional<Schedule> findByIdAndVehicleTypeId(String id, Long vehicleTypeId);

    @Query("{'regionFrom.slug': ?0, 'regionTo.slug': ?1}")
    List<Schedule> findByRegionFromAndRegionTo(String from, String to);

    @Override
    boolean existsById(String s);
}
