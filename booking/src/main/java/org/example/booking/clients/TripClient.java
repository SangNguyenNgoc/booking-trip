package org.example.booking.clients;

import org.example.booking.api.entities.Trip;
import org.example.booking.clients.dtos.TripResponse;
import org.example.booking.config.ClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

@FeignClient(
        name = "trip",
        configuration = ClientConfig.class
)
public interface TripClient {
    @GetMapping("/internal/trips/{id}")
    Optional<TripResponse> getTrip(
            @PathVariable String id,
            @RequestHeader("X-API-KEY") String apiKey
    );
}
