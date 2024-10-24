package org.example.vehicle.api.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.vehicle.utils.auditing.AuditorEntity;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "vehicle_types")
public class VehicleType extends AuditorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "number_of_rows", nullable = false)
    private Integer numberOfRows;

    @Column(name = "seat_per_row", nullable = false)
    private Integer seatsPerRow;

    @Column(name = "number_of_seats", nullable = false)
    private Integer numberOfSeats;

    @Column(name = "number_of_floors", nullable = false)
    private Integer numberOfFloors;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @OneToMany(
            mappedBy = "type",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    private Set<Vehicle> vehicles;

    @OneToMany(
            mappedBy = "vehicleType",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    private Set<Seat> seats;

}
