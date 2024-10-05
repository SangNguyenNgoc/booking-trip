package com.example.location.api.repositories;

import com.example.location.api.entities.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


import java.util.List;
import java.util.Optional;

public interface LocationRepository extends MongoRepository<Location, String> {

    boolean existsByHotline(String phoneNumber);

    Optional<Location> findBySlug(String slug);

    @Query(value = "{}", fields = "{'name':  1, 'address':  1, 'slug':  1}")
    List<Location> getAllLocationNames();

    Page<Location> findByRegionId(String regionId, Pageable pageable);

    List<Location> findByRegionSlug(String regionSlug);

    List<Location> findAllBySlugIn(List<String> slugs);

}