package org.example.statistics.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralStatistics {
    private Double revenue;
    private Double grossProfit;
    private Double ratio;
    private String type;
    private Integer time;
    private List<BillChart> billChart;
    private List<TripChart> tripChart;
}
