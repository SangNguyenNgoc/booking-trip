package org.tripservice.trip.api.dtos.seat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatDto implements Serializable {
    private Long id;
    private Integer rowNo;
    private Integer colNo;
    private Integer floorNo;
    private String name;
    private Boolean isReserved;
    private Long price;
}
