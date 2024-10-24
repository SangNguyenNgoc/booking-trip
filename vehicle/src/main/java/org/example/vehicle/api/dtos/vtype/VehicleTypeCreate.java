package org.example.vehicle.api.dtos.vtype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link org.example.vehicle.api.entities.VehicleType}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleTypeCreate implements Serializable {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Integer numberOfSeats;

    @NotNull
    private Integer numberOfRows;

    @NotNull
    private Integer seatsPerRow;

    @NotNull
    private Integer numberOfFloors;

    @NotNull
    private Long price;

    private List<EmptySeat> emptySeats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmptySeat {
        @NotNull
        private Integer floorNo;
        @NotNull
        private Integer rowNo;
        @NotNull
        private Integer colNo;
    }

}