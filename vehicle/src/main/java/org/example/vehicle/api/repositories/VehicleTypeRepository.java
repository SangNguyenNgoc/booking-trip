package org.example.vehicle.api.repositories;

import org.example.vehicle.api.entities.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {

    Optional<VehicleType> findByIdAndActiveTrue(Long id);

    List<VehicleType> findByActiveTrue();

}