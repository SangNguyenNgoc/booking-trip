package org.tripservice.trip.api.dtos.trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripStatistic {
    private String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalSeats;
    private Integer seatsAvailable;
    private Integer seatsReserved;
    private String licensePlate;
    private Long price;
    private String scheduleId;
}
