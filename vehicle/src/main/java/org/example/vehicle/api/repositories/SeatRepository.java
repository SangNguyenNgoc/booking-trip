package org.example.vehicle.api.repositories;

import org.example.vehicle.api.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}