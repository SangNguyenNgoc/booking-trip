package org.example.statistics.api.repositories;

import org.example.statistics.api.documents.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BillRepository extends MongoRepository<Bill, String> {
    @Query("{ 'createDate': { $gte: ?0, $lt: ?1 } }")
    List<Bill> findByCreateDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("{'tripId': {$eq: ?0}}")
    List<Bill> findBillByTripId(String id);
}
