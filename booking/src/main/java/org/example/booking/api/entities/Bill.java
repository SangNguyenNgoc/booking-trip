package org.example.booking.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "bills")
public class Bill {
    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "payment_at", nullable = true)
    private LocalDateTime paymentAt;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "payment_url", columnDefinition = "TEXT", nullable = false)
    private String paymentUrl;

    @Column(name = "failure_reason", nullable = true)
    private String failureReason;

    @Column(name = "failure_at", nullable = true)
    private LocalDateTime failureAt;

    @Column(name = "failure", nullable = true)
    private Boolean failure;

    @Column(name = "profile_id")
    private String profileId;

    @Column(name = "passenger_name", nullable = false)
    private String passengerName;

    @Column(name = "passenger_phone", nullable = false)
    private String passengerPhone;

    @Column(name = "passenger_email", nullable = false)
    private String passengerEmail;

    @OneToMany(
            mappedBy = "bill",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    private Set<Ticket> tickets;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "status_id",
            nullable = false,
            referencedColumnName = "id"
    )
    private BillStatus status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Trip trip;
}
