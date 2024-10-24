package org.tripservice.trip.api.dtos.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tripservice.trip.api.documents.Trip;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {
    private String tripId;
    private List<String> seats;
}
