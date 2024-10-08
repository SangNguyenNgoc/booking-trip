package org.example.vehicle.api.repositories;

import org.example.vehicle.api.entities.MaintainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintainScheduleRepository extends JpaRepository<MaintainSchedule, Long> {
}