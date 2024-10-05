package com.example.location.api.dtos.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripScheduleRequest {

    private String from;
    private String to;
    private List<String> pickUps;
    private List<String> transits;
}
