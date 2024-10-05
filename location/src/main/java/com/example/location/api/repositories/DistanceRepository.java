package com.example.location.api.repositories;

import com.example.location.api.entities.Distance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface DistanceRepository extends MongoRepository<Distance, String> {

    @Query("""
            { $or: [
                { 'from': ?0, 'to': ?1 },
                { 'from': ?1, 'to': ?0 }
            ] }
    """)
    Optional<Distance> findByFromAndToOrToAndFrom(String from, String to);
}
