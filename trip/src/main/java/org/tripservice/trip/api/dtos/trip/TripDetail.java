package org.tripservice.trip.api.dtos.trip;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tripservice.trip.api.dtos.schedule.ScheduleDetail;

import java.io.Serializable;
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
    private List<SeatDto> seats;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ScheduleDetail schedule;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SeatDto implements Serializable {
        private Long id;
        private Integer rowNo;
        private Integer colNo;
        private Integer floorNo;
        private String name;
        private Boolean isReserved;
    }

}
