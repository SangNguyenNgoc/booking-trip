package org.example.vehicle.api.dtos.vtype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link org.example.vehicle.api.entities.VehicleType}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleTypeInfo implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Integer numberOfRows;
    private Integer seatsPerRow;
}