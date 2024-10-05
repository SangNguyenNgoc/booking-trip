package com.example.location.utils.clients;

import com.example.location.utils.clients.dtos.RouteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hereRoutingClient", url = "https://router.hereapi.com/v8/routes")
public interface RoutingClient {

    @GetMapping
    RouteResponse getRoute(@RequestParam("transportMode") String transportMode,
                           @RequestParam("origin") String origin,
                           @RequestParam("destination") String destination,
                           @RequestParam("return") String returnParam,
                           @RequestParam("apiKey") String apiKey);
}
