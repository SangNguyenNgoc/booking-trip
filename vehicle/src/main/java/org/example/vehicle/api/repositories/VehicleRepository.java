package org.example.vehicle.api.repositories;

import org.example.vehicle.api.entities.Vehicle;
import org.example.vehicle.api.entities.VehicleType;
import org.hibernate.query.spi.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("select v.licensePlate from vehicles v")
    List<String> findAllLicensePlates();

    @Query("select v.licensePlate from vehicles v where v.type.id = ?1")
    List<String> findAllLicensePlatesByType(Long typeId);

    @Query(value = "SELECT v.current_location FROM vehicle.vehicles v WHERE v.current_location LIKE CONCAT('%', ?1, '%') LIMIT 1", nativeQuery = true)
    Optional<String> findFirstByCurrentLocation(String location);

    boolean existsByLicensePlate(String licensePlate);

    @Transactional
    @Modifying
    @Query("update vehicles v set v.active = ?1 where v.type = ?2")
    int updateActiveByType(Boolean active, VehicleType type);


}