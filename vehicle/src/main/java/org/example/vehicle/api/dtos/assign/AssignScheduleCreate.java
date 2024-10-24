package org.example.vehicle.api.dtos.assign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignScheduleCreate {

    private LocalDate startDate;
    private LocalDate endDate;
    private String route;
    private Long vehicleId;
}
