package org.tripservice.trip.api.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.tripservice.trip.api.documents.Schedule;

import java.util.List;

public interface ScheduleRepository extends MongoRepository<Schedule, String> {

    @Query(fields = "{from.slug: ?1, to.slug:  ?2}")
    List<Schedule> findByFromAndTo(String from, String to);

    @Override
    boolean existsById(String s);
}
