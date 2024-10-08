package org.example.vehicle.api.repositories;

import org.example.vehicle.api.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("select v.licensePlate from vehicles v")
    List<String> getAllLicensePlates();

    @Query("select v.licensePlate from vehicles v where v.type.id = ?1")
    List<String> getAllLicensePlatesByType(Long typeId);

    @Query("select v.currentLocation from vehicles v where v.currentLocation like concat('%', ?1, '%')")
    String getLocationInVehicle(String location);
}