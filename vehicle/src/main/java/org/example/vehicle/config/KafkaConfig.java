package org.example.vehicle.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JsonMessageConverter;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic vehicleTypeIsCreated() {
        return new NewTopic("vehicleTypeIsCreated", 1, (short) 1);
    }

    @Bean
    public JsonMessageConverter converter() {
        return new JsonMessageConverter();
    }
}
