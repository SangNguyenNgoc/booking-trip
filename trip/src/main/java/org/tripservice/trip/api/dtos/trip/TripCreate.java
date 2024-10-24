package org.tripservice.trip.api.dtos.trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripCreate {
    private String scheduleId;
    private String contraryScheduleId;
    private List<String> vehicles;
    private Long vehicleTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
}
