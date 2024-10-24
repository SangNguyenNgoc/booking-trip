package org.tripservice.trip.api.dtos.trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripInfo {
    private String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer seatsAvailable;
    private Long price;
    private String vehicleTypeName;

}
