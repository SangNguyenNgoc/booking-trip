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
    private Double price;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ScheduleDTO {
        private FromToDTO from;
        private FromToDTO to;
        private RegionDTO regionFrom;
        private RegionDTO regionTo;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FromToDTO {
        private String name;
        private String address;
        private String slug;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RegionDTO {
        private String id;
        private String name;
        private String slug;
        private String type;
        private String nameWithType;
        private int code;

        // Getters and Setters
    }
}
