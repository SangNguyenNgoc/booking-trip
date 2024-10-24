package org.example.booking.api.repositories;

import org.example.booking.api.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    @Query("""
            select t from Ticket t
            where t.trip.id = ?1
            and ( (t.bill.expireAt > ?2 and t.bill.status.id = 1) or t.bill.status.id = 2 )
            """)
    List<Ticket> findByTripId(String tripId, LocalDateTime dateTime);
}
