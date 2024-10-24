package org.example.vehicle.api.repositories;

import org.example.vehicle.api.entities.Vehicle;
import org.example.vehicle.api.entities.VehicleType;
import org.example.vehicle.api.entities.enums.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("select v.licensePlate from vehicles v")
    List<String> findAllLicensePlates();

    List<Vehicle> findAllByLicensePlateIn(List<String> licensePlates);

    @Query("select v.licensePlate from vehicles v where v.type.id = ?1")
    List<String> findAllLicensePlatesByType(Long typeId);

    @Query(value = "SELECT v.current_location FROM vehicle.vehicles v WHERE v.current_location LIKE CONCAT('%', ?1, '%') LIMIT 1", nativeQuery = true)
    Optional<String> findFirstByCurrentLocation(String location);

    boolean existsByLicensePlate(String licensePlate);

    @Transactional
    @Modifying
    @Query("update vehicles v set v.active = ?1 where v.type = ?2")
    void updateActiveByType(Boolean active, VehicleType type);

    Optional<Vehicle> findByIdAndActiveTrue(Long id);

    @Query("select v from vehicles v where v.belongTo like concat('%', :location, '%') ")
    Page<Vehicle> findAllByBelongTo(@Param("location") String belongTo, Pageable pageable);

    @Query("select v from vehicles v where v.belongTo like concat('%', :location, '%') ")
    List<Vehicle> findAllByBelongTo(@Param("location") String belongTo);


    @Query("select v from vehicles v " +
            "where v.belongTo like concat('%', :location, '%') " +
            "and v.type.id = :typeId")
    Page<Vehicle> findAllByBelongToAndType(
            @Param("location") String belongTo,
            @Param("typeId") Long typeId,
            Pageable pageable
    );

    @Query("select v from vehicles v " +
            "where v.belongTo like concat('%', :location, '%') " +
            "and v.type.id = :typeId")
    List<Vehicle> findAllByBelongToAndType(
            @Param("location") String belongTo,
            @Param("typeId") Long typeId
    );

    @Query("select v from vehicles v " +
            "where v.belongTo like concat('%', :location, '%') " +
            "and v.type.id = :typeId " +
            "and v.status = :status ")
    Page<Vehicle> findAllByBelongAndTypeAndStatus(
            @Param("location") String belongTo,
            @Param("typeId") Long typeId,
            @Param("status") VehicleStatus status,
            Pageable pageable
    );

    @Query("select v from vehicles v " +
            "where v.belongTo like concat('%', :location, '%') " +
            "and v.type.id = :typeId " +
            "and v.status = :status ")
    List<Vehicle> findAllByBelongAndTypeAndStatus(
            @Param("location") String belongTo,
            @Param("typeId") Long typeId,
            @Param("status") VehicleStatus status
    );


}