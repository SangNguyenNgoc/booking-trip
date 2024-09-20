package com.example.location.api.entities;

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

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "name_with_type", nullable = false)
    private String nameWithType;

    @Column(name = "code", nullable = false)
    private Integer code;

    @OneToMany(
            mappedBy = "region",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    private Set<Location> locations;

}
