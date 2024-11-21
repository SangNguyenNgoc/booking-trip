package org.tripservice.trip.api.dtos.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleGroup {
    private ScheduleDetail schedule;
    private ScheduleDetail contrarySchedule;
}
