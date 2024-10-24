package org.example.booking.api.entities;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @Column(name = "ticket_id", nullable = false)
    private String id;

    @Column(name = "seat_id")
    private Long seatId;

    @Column(name = "price")
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "bill_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Bill bill;
}
