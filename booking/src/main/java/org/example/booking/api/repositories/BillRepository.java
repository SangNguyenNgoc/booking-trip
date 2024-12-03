package org.example.booking.api.repositories;

import org.example.booking.api.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, String>, JpaSpecificationExecutor<Bill> {
    @Query("select b from Bill b where b.id = ?1 and b.status.id = ?2")
    Optional<Bill> findByIdAndStatusId(String id, Integer id1);

    @Query("select b from Bill b " +
            "join fetch b.trip t " +
            "join fetch b.tickets tk " +
            "left join fetch b.roundTrip " +
            "where b.id =?1 and b.profileId = ?2")
    Optional<Bill> findByIdAndProfileId(String id, String profileId);

    @Query("select b from Bill b where " +
            "(b.failure = true or b.expireAt < ?1) " +
            "and b.status.id = 1")
    Optional<List<Bill>> findByExpireAtAndStatus(LocalDateTime toNow);

    @Query("select b from Bill b " +
            "join fetch b.trip t " +
            "join fetch b.tickets tk " +
            "left join fetch b.roundTrip " +
            "where b.profileId = ?1")
    List<Bill> findBillByProfileId(String profileId);

    @Query("select b from Bill b " +
            "join fetch b.trip t " +
            "join fetch b.tickets tk " +
            "where b.id = ?1")
    Optional<Bill> findBillById(String id);

    @Query("select b from Bill b " +
            "join fetch b.trip t " +
            "join fetch b.tickets tk " +
            "where b.passengerPhone like concat('%', ?2, '%') and b.id = ?1")
    Optional<Bill> findBillByPhoneNumberAndId(String billId, String phoneNumber);

    @Query("select b from Bill b " +
            "join fetch b.trip t " +
            "join fetch b.tickets tk " +
            "left join fetch b.roundTrip " +
            "where b.id =?1 and b.profileId is null")
    Optional<Bill> findByIdAndProfileIdIsNull(String id);
}
