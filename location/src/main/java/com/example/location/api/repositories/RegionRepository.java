package com.example.location.api.repositories;

import com.example.location.api.entities.Region;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RegionRepository extends MongoRepository<Region, String> {

    Optional<Region> findBySlug(String slug);
}