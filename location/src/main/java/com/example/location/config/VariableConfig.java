package com.example.location.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VariableConfig {

    public final String GEOCODING_API_KEY;
    public final String LOCATION_API_KEY;

    public VariableConfig(
            @Value("${geocoding.api-key}") String geocodingApiKey,
            @Value("${internal-api-key.location}") String locationApiKey
    ) {
        GEOCODING_API_KEY = geocodingApiKey;
        LOCATION_API_KEY = locationApiKey;
    }
}
