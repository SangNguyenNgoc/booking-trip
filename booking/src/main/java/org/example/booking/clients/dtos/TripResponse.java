package org.example.booking.clients.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripResponse {
    private String id;
    private String startTime;
    private String endTime;
    private int seatsAvailable;
    private String licensePlate;
    private ScheduleDTO schedule;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ScheduleDTO {
        private Double price;
    }
}
