package org.example.booking.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "bill_status")
public class BillStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(
            mappedBy = "status",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private Set<Bill> bills;
}
