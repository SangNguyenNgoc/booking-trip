package org.example.booking.api.repositories;

import org.example.booking.api.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, String> {
    @Query("select b from Bill b where b.id = ?1 and b.status.id = ?2")
    Optional<Bill> findByIdAndStatusId(String id, Integer id1);
}
