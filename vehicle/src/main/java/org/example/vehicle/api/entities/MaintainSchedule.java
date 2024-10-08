package org.example.vehicle.api.entities;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.*;
import org.example.vehicle.api.entities.enums.MaintainType;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "maintain_schedule")
public class MaintainSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "cost", nullable = false)
    private Double cost;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MaintainType type;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "vehicle_id",
            nullable = false,
            referencedColumnName = "id"
    )
    private Vehicle vehicle;
}
