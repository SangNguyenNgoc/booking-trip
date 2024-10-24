package org.tripservice.trip.api.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.tripservice.trip.api.documents.Schedule;

import java.util.List;

public interface ScheduleRepository extends MongoRepository<Schedule, String> {

    @Query("{'from.slug': ?0, 'to.slug': ?1}")
    List<Schedule> findByFromAndTo(String from, String to);

    @Query("{'regionFrom.slug': ?0, 'regionTo.slug': ?1}")
    List<Schedule> findByRegionFromAndRegionTo(String from, String to);

    @Override
    boolean existsById(String s);
}
