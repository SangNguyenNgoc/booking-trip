package org.example.vehicle.api.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.vehicle.api.entities.enums.VehicleStatus;
import org.example.vehicle.utils.auditing.AuditorEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "vehicles")
public class Vehicle extends AuditorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "license_plate", nullable = false, length = 20, unique = true)
    private String licensePlate;

    @Column(name = "manufacturing_date", nullable = false)
    private LocalDate manufacturingDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    @Column(name = "current_location", nullable = true, length = 500)
    private String currentLocation;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "type_id",
            nullable = false,
            referencedColumnName = "id"
    )
    private VehicleType type;

    @OneToMany(
            mappedBy = "vehicle",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    private Set<MaintainSchedule> maintainSchedules;

}
