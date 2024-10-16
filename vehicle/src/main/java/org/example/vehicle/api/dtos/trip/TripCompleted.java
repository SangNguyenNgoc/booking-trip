package org.example.vehicle.api.dtos.trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripCompleted {
    private Long vehicleId;
    private String locationSlug;
    private Long arrivalTime;
}
