package org.example.vehicle.api.dtos.vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link org.example.vehicle.api.entities.Vehicle}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleCreate implements Serializable {

    @NotBlank
    @Pattern(regexp = "^[1-9][0-9][A-Z]-\\d{4,5}$\n")
    private String licensePlate;

    @NotNull
    private LocalDate manufacturingDate;

    @NotBlank
    private String currentLocationSlug;

    @NotBlank
    private Long typeId;
}