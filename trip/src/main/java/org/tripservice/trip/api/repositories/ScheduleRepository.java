package org.tripservice.trip.api.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.tripservice.trip.api.documents.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends MongoRepository<Schedule, String> {

    @Query("{'from.slug': ?0, 'to.slug': ?1}")
    List<Schedule> findByFromAndTo(String from, String to);

    Optional<Schedule> findByIdAndVehicleTypeId(String id, Long vehicleTypeId);

    @Query("{'regionFrom.slug': ?0, 'regionTo.slug': ?1}")
    List<Schedule> findByRegionFromAndRegionTo(String from, String to);

    @Query("{'regionFrom.slug': ?0, 'regionTo.slug': ?1, 'vehicleTypeId': { $in: ?2 }}")
    List<Schedule> findByRegionFromAndRegionTo(String from, String to, List<Long> vehicleTypeId);

    @Query(value = "{'regionFrom.slug' : ?0}", sort = "{'bookedCount': -1}")
    List<Schedule> findTop3ByRegionFromOrderByBookedCountDesc(String regionFrom, Pageable pageable);

    @Override
    boolean existsById(String s);
}
