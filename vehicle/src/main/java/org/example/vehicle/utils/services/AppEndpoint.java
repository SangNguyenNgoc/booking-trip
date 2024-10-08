package org.example.vehicle.utils.services;

import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

public class AppEndpoint {

    public record EndpointPermission(String path, HttpMethod method) {}

    public static final List<EndpointPermission> PUBLIC_ENDPOINTS = Arrays.asList(

    );
}
