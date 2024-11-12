package org.tripservice.trip.api.dtos.seat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatRow {
    private Integer floorNo;
    private Integer rowId;
    private List<SeatDto> seats;
}
