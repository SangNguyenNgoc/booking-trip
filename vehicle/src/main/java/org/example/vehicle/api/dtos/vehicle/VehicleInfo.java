package org.example.vehicle.api.dtos.vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vehicle.api.entities.enums.VehicleStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for {@link org.example.vehicle.api.entities.Vehicle}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleInfo implements Serializable {
    private Long id;
    private String licensePlate;
    private LocalDate manufacturingDate;
    private VehicleStatus status;
    private LocationDto nowAt;
    private LocationDto belong;
    private LocalDateTime lastArrivalAt;
    private Boolean active;
    private VehicleTypeDto type;

    /**
     * DTO for {@link org.example.vehicle.api.entities.VehicleType}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleTypeDto implements Serializable {
        private Long id;
        private String name;
        private String description;
        private Integer numberOfRows;
        private Integer seatsPerRow;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocationDto implements Serializable {
        private String name;
        private String address;
        private String slug;
    }


}