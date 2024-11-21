package org.example.statistics.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillChart {
    private Integer numberOfBill;
    private Double revenue;
    private Double ratio;
    private String type;
    private Integer time;
}
