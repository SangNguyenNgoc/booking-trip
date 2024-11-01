package org.example.booking.api.repositories;

import org.example.booking.api.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, String> {
    @Query("select b from Bill b where b.id = ?1 and b.status.id = ?2")
    Optional<Bill> findByIdAndStatusId(String id, Integer id1);

    @Query("select b from Bill b where " +
            "(b.expireAt < ?1 and b.status.id = 1) " +
            "or b.failure = true ")
    Optional<List<Bill>> findByExpireAtAndStatus(LocalDateTime toNow);

    @Query("select b from Bill b " +
            "join fetch b.trip t " +
            "join fetch b.tickets tk" +
            " where b.profileId = ?1")
    List<Bill> findBillByProfileId(String profileId);

    @Query("select b from Bill b " +
            "join fetch b.trip t " +
            "join fetch b.tickets tk " +
            "where b.passengerPhone like concat('%', ?1, '%')")
    List<Bill> findBillByPhoneNumber(String phoneNumber);
}
