package org.example.vehicle.api.dtos.vtype;

import jakarta.validation.constraints.NotNull;
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
public class VehicleTypeUpdate implements Serializable {
    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String description;
}