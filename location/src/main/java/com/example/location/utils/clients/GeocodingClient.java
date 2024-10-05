package com.example.location.utils.clients;

import com.example.location.config.ClientConfig;
import com.example.location.utils.clients.dtos.GeocodingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "hereGeocodingClient",
        url = "https://geocode.search.hereapi.com/v1/geocode",
        configuration = ClientConfig.class
)
public interface GeocodingClient {

    @GetMapping
    GeocodingResponse getCoordinates(@RequestParam("q") String address,
                                     @RequestParam("apiKey") String apiKey);
}
