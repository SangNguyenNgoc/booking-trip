package org.example.booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VariableConfig {
    public final String TRIP_API_KEY;
    public final String BOOKING_API_KEY;

    public VariableConfig(
            @Value("${internal-api-key.trip}") String locationApiKey,
            @Value("${internal-api-key.booking}") String bookingApiKey
    ) {
        TRIP_API_KEY = locationApiKey;
        BOOKING_API_KEY = bookingApiKey;
    }
}
