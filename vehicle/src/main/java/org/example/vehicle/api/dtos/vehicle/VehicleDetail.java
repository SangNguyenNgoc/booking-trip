package org.example.vehicle.api.dtos.vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vehicle.api.entities.enums.MaintainType;
import org.example.vehicle.api.entities.enums.VehicleStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link org.example.vehicle.api.entities.Vehicle}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDetail implements Serializable {
    private Long id;
    private String licensePlate;
    private LocalDate manufacturingDate;
    private VehicleStatus status;
    private String currentLocation;
    private VehicleTypeDto type;
    private List<MaintainScheduleDto> maintainSchedules;

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

    /**
     * DTO for {@link org.example.vehicle.api.entities.MaintainSchedule}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaintainScheduleDto implements Serializable {
        private Long id;
        private LocalDate startDate;
        private LocalDate endDate;
        private String content;
        private Double cost;
        private MaintainType type;
    }
}