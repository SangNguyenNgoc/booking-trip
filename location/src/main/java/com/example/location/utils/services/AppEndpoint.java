package com.example.location.utils.services;

import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

public class AppEndpoint {

    public record EndpointPermission(String path, HttpMethod method) {}

    public static final List<EndpointPermission> PUBLIC_ENDPOINTS = Arrays.asList(
            new EndpointPermission("/locations", HttpMethod.GET),
            new EndpointPermission("/locations/{slug}", HttpMethod.GET),
            new EndpointPermission("/locations/region/{regionId}", HttpMethod.GET),
            new EndpointPermission("/locations/names", HttpMethod.GET),
            new EndpointPermission("/locations/names/{slug}", HttpMethod.GET),
            new EndpointPermission("/locations/trip", HttpMethod.GET),
            new EndpointPermission("/regions", HttpMethod.GET),
            new EndpointPermission("/regions/{slug}", HttpMethod.GET)

    );


}
