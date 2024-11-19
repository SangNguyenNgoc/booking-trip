package org.example.statistics.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripStatistics implements Serializable {
    private Integer numberOfTicket;
    private Double occupancyRate;
    private Double revenue;
    private LocalDateTime startTime;
}
