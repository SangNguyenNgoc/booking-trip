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
@Table(name = "trips")
public class Trip {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "start_at")
    private String startAt;

    @Column(name = "end_at")
    private String endAt;

    @Column(name = "price")
    private Double price;

    @OneToMany(
            mappedBy = "trip",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    private Set<Ticket> tickets;

}
