package org.example.vehicle.config;


import feign.codec.ErrorDecoder;
import org.example.vehicle.clients.CustomErrorDecoder;
import org.springframework.context.annotation.Bean;

public class ClientConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
