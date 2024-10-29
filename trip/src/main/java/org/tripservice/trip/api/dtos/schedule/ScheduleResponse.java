package org.tripservice.trip.api.dtos.schedule;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tripservice.trip.api.dtos.location.LocationName;
import org.tripservice.trip.api.dtos.location.RegionInfo;
import org.tripservice.trip.api.dtos.trip.TripInfo;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
    private String id;
    private RegionInfo regionFrom;
    private RegionInfo regionTo;
    private LocationName from;
    private LocationName to;
    private Double duration;
    private Double distance;
    private Long price;
    private Long vehicleTypeId;
    private String vehicleTypeName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TripInfo> trips;
}
