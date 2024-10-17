package org.tripservice.trip.config;


import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.tripservice.trip.clients.CustomErrorDecoder;

public class ClientConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
