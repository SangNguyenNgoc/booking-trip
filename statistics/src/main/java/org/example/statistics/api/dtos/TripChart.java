package org.example.statistics.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripChart {
    private Integer numberOfTrip;
    private Double ratio;
    private Double occupancyRate;
    private String type;
    private Integer time;
}
