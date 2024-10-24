package org.tripservice.trip.api.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "vehicle_types")
public class VehicleType implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Integer numberOfRows;
    private Integer seatsPerRow;
    private Boolean active;
    private List<Seat> seats;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Seat implements Serializable {
        private Long id;
        private Integer rowNo;
        private Integer colNo;
        private Integer floorNo;
        private String name;
    }
}