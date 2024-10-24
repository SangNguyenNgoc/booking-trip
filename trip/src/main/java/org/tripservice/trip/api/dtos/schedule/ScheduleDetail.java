package org.tripservice.trip.api.dtos.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tripservice.trip.api.documents.VehicleType;
import org.tripservice.trip.api.dtos.location.LocationName;
import org.tripservice.trip.api.dtos.location.RegionInfo;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDetail {
    private String id;
    private RegionInfo regionFrom;
    private RegionInfo regionTo;
    private LocationName from;
    private LocationName to;
    private List<ScheduleItemInfo> pickUps;
    private List<ScheduleItemInfo> transits;
    private Double duration;
    private Double distance;
    private Long price;
    private VehicleTypeDto vehicleType;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleTypeDto {
        private Long id;
        private String name;
        private String description;
        private Integer numberOfRows;
        private Integer seatsPerRow;
    }
}
