package org.example.booking.api.repositories;

import org.example.booking.api.entities.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillStatusRepository extends JpaRepository<BillStatus, Integer > {
}
