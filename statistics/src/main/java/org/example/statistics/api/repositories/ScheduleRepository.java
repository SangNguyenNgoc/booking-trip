package org.example.statistics.api.repositories;

import org.example.statistics.api.documents.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleRepository extends MongoRepository<Schedule, String> {
}
