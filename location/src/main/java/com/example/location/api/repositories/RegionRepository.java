package com.example.location.api.repositories;

import com.example.location.api.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, String> {

    Optional<Region> findBySlug(String slug);
}