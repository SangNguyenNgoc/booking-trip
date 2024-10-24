package org.tripservice.trip.api.dtos.schedule;

import lombok.*;

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
