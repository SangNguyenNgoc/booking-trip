package org.tripservice.trip.api.dtos.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignSchedule {

    private LocalDate startDate;

    private LocalDate endDate;

    private String route;

    private String licensePlate;

}
