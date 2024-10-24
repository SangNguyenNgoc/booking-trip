package org.tripservice.trip.api.controllers.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tripservice.trip.api.dtos.trip.TripDetail;
import org.tripservice.trip.api.services.interfaces.TripService;

@RestController
@RequestMapping("/internal/trips")
@RequiredArgsConstructor
public class InternalTripController {
    private final TripService tripService;

    @GetMapping("/{id}")
    public ResponseEntity<TripDetail> getTripsByFromAndToInDate(
            @PathVariable("id") String tripId
    ) {
        return ResponseEntity.ok(tripService.getTripDetailForBooking(tripId));
    }
}
