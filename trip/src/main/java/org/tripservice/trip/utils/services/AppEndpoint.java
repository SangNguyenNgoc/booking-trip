package org.tripservice.trip.utils.services;

import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

public class AppEndpoint {

    public record EndpointPermission(String path, HttpMethod method) {}

    public static final List<EndpointPermission> PUBLIC_ENDPOINTS = List.of(
            new EndpointPermission("/schedules", HttpMethod.GET)
    );


}
