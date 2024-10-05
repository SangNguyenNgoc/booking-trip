package com.example.location.api.dtos.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripScheduleResponse {

    private LocationName from;
    private LocationName to;
    private List<ScheduleInfo> pickUps;
    private List<ScheduleInfo> transits;
    private Double duration;
    private Double distance;
}
