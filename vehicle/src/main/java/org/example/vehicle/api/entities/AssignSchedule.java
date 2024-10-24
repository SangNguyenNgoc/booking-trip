package org.example.vehicle.api.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.vehicle.api.entities.enums.MaintainType;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "assign_schedule")
public class AssignSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "route", length = 100, nullable = false)
    private String route;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "vehicle_id",
            nullable = false,
            referencedColumnName = "id"
    )
    private Vehicle vehicle;

}
