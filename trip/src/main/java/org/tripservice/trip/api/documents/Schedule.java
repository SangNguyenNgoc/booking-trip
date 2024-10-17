package org.tripservice.trip.api.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.tripservice.trip.api.dtos.schedule.LocationName;
import org.tripservice.trip.api.dtos.schedule.ScheduleInfo;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "schedules")
public class Schedule {
    private String id;
    private LocationName from;
    private LocationName to;
    private List<ScheduleInfo> pickUps;
    private List<ScheduleInfo> transits;
    private Double duration;
    private Double distance;
}
