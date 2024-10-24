package org.example.vehicle.api.repositories;

import org.example.vehicle.api.entities.AssignSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignScheduleRepository extends JpaRepository<AssignSchedule, Long> {
}