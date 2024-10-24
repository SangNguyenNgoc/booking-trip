package org.example.vehicle.api.dtos.vtype;

import lombok.AllArgsConstructor;
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
public class VehicleTypeDetail implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Integer numberOfRows;
    private Integer seatsPerRow;
    private Boolean active;
    private Long price;
    private List<SeatDto> seats;

    /**
     * DTO for {@link org.example.vehicle.api.entities.Seat}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SeatDto implements Serializable {
        private Long id;
        private Integer rowNo;
        private Integer colNo;
        private Integer floorNo;
        private String name;
    }
}