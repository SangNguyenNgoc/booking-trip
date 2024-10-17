package org.tripservice.trip.api.dtos.schedule;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String from;
    @NotBlank
    private String to;

    private List<String> pickUps;

    private List<String> transits;
}
