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

    @Column(name = "region_from_name")
    private String regionFromName;

    @Column(name = "region_to_name")
    private String regionToName;

    @Column(name = "location_from_name")
    private String locationFromName;

    @Column(name = "location_to_name")
    private String locationToName;

    @OneToMany(
            mappedBy = "trip",
            fetch = FetchType.EAGER,
            cascade = CascadeType.PERSIST
    )
    private Set<Bill> bills;

}
