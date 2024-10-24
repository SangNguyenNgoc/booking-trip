package org.tripservice.trip.utils.services;

import org.springframework.http.HttpMethod;

import java.util.List;

public class AppEndpoint {

    public static final List<EndpointPermission> PUBLIC_ENDPOINTS = List.of(
            new EndpointPermission("/schedules", HttpMethod.GET),
            new EndpointPermission("/trips", HttpMethod.GET),
            new EndpointPermission("/trips/{id}", HttpMethod.GET)
    );

    public record EndpointPermission(String path, HttpMethod method) {
    }


}
