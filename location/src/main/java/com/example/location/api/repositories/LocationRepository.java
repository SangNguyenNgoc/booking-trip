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

    Optional<Location> findBySlugAndType(String slug, String type);

    @Query(value = "{}", fields = "{'name':  1, 'address':  1, 'slug':  1}")
    List<Location> findAllLocationNames();

    @Query(value = "{slug: ?1}", fields = "{'name':  1, 'address':  1, 'slug':  1}")
    Optional<Location> findLocationNameBySlug(String slug);


    List<Location> findByRegionId(String regionId);

    Page<Location> findByRegionId(String regionId, Pageable pageable);

    List<Location> findByRegionSlug(String regionSlug);

    List<Location> findAllBySlugIn(List<String> slugs);

}