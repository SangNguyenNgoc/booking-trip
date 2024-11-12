package org.tripservice.trip.api.dtos.trip;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tripservice.trip.api.dtos.schedule.ScheduleDetail;
import org.tripservice.trip.api.dtos.seat.SeatRow;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDetail {
    private String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer seatsAvailable;
    private String licensePlate;
    private Long price;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ScheduleDetail schedule;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<List<SeatRow>> seats;

}
