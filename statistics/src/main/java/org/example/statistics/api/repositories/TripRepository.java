package org.example.statistics.api.repositories;

import org.example.statistics.api.documents.Trip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends MongoRepository<Trip, String> {
    @Query("{ 'startTime': { $gte: ?0, $lt: ?1 } }")
    List<Trip> findByStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("{$and: [{'scheduleId': ?0}, {'startTime': {$gte: ?1, $lt: ?2}}]}")
    List<Trip> findByScheduleIdAndStartTimeBetween(String scheduleId, LocalDateTime from, LocalDateTime to);
}
