package org.example.vehicle.clients;

import org.example.vehicle.config.ClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(
        name = "location",
        configuration = ClientConfig.class
)
public interface LocationClient {

    @GetMapping("/locations/names/{slug}")
    Optional<String> getLocation(@PathVariable String slug);
}
