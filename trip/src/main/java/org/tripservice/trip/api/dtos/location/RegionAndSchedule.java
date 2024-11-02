package org.tripservice.trip.api.dtos.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionAndSchedule {
    private String name;
    private String slug;
    private String nameWithType;
    private List<ScheduleDto> schedules;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleDto {
        private String id;
        private RegionInfo regionTo;
        private Double duration;
        private Double distance;
        private Long price;
        private Long vehicleTypeId;
        private String vehicleTypeName;

    }
}
