package org.tripservice.trip.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.tripservice.trip.api.documents.VehicleType;
import org.tripservice.trip.config.ClientConfig;

import java.util.Optional;

@FeignClient(
        name = "vehicle",
        configuration = ClientConfig.class
)
public interface VehicleClient {

    @GetMapping("/internal/vtypes/{id}")
    Optional<VehicleType> findById(
            @PathVariable("id") Long id,
            @RequestHeader("X-API-KEY") String apiKey
    );

}
