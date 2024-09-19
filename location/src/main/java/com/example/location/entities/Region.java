package com.example.location.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "regisons")
public class Region {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(
            mappedBy = "region",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    private Set<Location> locations;

}
