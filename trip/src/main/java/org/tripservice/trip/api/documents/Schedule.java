package org.tripservice.trip.api.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.tripservice.trip.api.dtos.location.LocationName;
import org.tripservice.trip.api.dtos.location.RegionInfo;
import org.tripservice.trip.api.dtos.schedule.ScheduleItemInfo;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "schedules")
public class Schedule {
    private String id;
    private RegionInfo regionFrom;
    private RegionInfo regionTo;
    private LocationName from;
    private LocationName to;
    private List<ScheduleItemInfo> pickUps;
    private List<ScheduleItemInfo> transits;
    private Double duration;
    private Double distance;
    private Long bookedCount;
    private Long vehicleTypeId;
    private String vehicleTypeName;
}
