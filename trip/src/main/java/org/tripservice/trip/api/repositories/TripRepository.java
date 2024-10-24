package org.tripservice.trip.api.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.tripservice.trip.api.documents.Trip;

import java.time.LocalDate;
import java.util.List;

public interface TripRepository extends MongoRepository<Trip, String> {

    @Query(value = "{ 'scheduleId': { $in: ?0 }, 'startTime': { $gte: ?1, $lt: ?2 } }",
            fields = "{ 'startTime': 1, 'endTime': 1, 'seatsAvailable':  1, 'scheduleId': 1 } ")
    List<Trip> findTripsBySchedulesAndStartTime(List<String> schedules, LocalDate startDate, LocalDate endDate, Sort sort);

}
