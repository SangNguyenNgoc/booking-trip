package com.example.location.api.repositories;

import com.example.location.api.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Location> findBySlug(String slug);

    @Query("select l from locations l where l.active = true")
    List<Location> getAllLocationNames();
}