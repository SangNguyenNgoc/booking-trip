package org.tripservice.trip.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VariableConfig {

    public final String TRIP_API_KEY;
    public final String LOCATION_API_KEY;
    public final String VEHICLE_API_KEY;

    public VariableConfig(
            @Value("${internal-api-key.trip}") String tripApiKey,
            @Value("${internal-api-key.location}") String locationApiKey,
            @Value("${internal-api-key.vehicle}") String vehicleApiKey

    ) {
        TRIP_API_KEY = tripApiKey;
        LOCATION_API_KEY = locationApiKey;
        VEHICLE_API_KEY = vehicleApiKey;
    }
}
