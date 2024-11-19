package org.tripservice.trip.api.dtos.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScheduleStatistic {
    private String id;
    private String regionFrom;
    private String regionTo;
    private String from;
    private String to;
    private Long price;
}
