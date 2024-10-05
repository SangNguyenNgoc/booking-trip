package com.example.location.api.dtos.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleInfo {
    private String name;
    private String address;
    private String slug;
    private Double latitude;
    private Double longitude;
    private Double distanceToLocation;
    private Double durationToLocation;
}
