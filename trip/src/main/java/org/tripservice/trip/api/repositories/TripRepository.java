package org.tripservice.trip.api.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.tripservice.trip.api.documents.Trip;

import java.time.LocalDate;
import java.util.List;

public interface TripRepository extends MongoRepository<Trip, String> {

    @Query(value = "{ 'scheduleId': { $in: ?0 }, 'startTime': { $gte: ?1, $lt: ?2 } }",
            fields = "{ 'startTime': 1, 'endTime': 1, 'seatsAvailable':  1, 'scheduleId': 1, 'price': 1, 'vehicleTypeName': 1, 'seatsReserved': 1, 'firstFloorSeats': 1, 'secondFloorSeats': 1 } ")
    List<Trip> findTripsBySchedulesAndStartTime(List<String> schedules, LocalDate startDate, LocalDate endDate, Sort sort);

    @Query(value = "{'licensePlate': { $in: ?0 }, 'startTime': { $gte: ?1, $lt: ?2 } }", fields = "{ '_id': 1 }")
    List<String> checkVehicleInTime(List<String> licensePlates, LocalDate startDate, LocalDate endDate);

    @Query(value = "{'startTime': { $gte: ?0, $lt: ?1 } }", fields = "{ 'startTime': 1, 'endTime': 1, 'scheduleId': 1 }")
    List<Trip> findAllByStartTimeBeforeAndStartTimeAfter(LocalDate startTime, LocalDate endTime);

}
