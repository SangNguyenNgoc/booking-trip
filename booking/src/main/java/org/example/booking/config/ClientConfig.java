package org.example.booking.config;


import feign.codec.ErrorDecoder;
import org.example.booking.clients.CustomErrorDecoder;
import org.springframework.context.annotation.Bean;

public class ClientConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
