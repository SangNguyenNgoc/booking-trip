package org.example.vehicle.clients;

import org.example.vehicle.config.ClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

@FeignClient(
        name = "location",
        configuration = ClientConfig.class
)
public interface LocationClient {

    @GetMapping("/internal/locations/names/{slug}")
    Optional<String> getLocation(@PathVariable String slug, @RequestHeader("X-API-KEY") String apiKey);
}
