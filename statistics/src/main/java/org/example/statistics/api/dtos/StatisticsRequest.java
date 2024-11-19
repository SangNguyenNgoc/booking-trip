package org.example.statistics.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.statistics.api.documents.Type;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsRequest {
    private Integer year;
    private Integer time;
    private Type type;
    private Type typeChart;
}
