package org.tripservice.trip.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JsonMessageConverter;

@Configuration
public class KafkaConfig {

    @Bean
    public JsonMessageConverter converter() {
        return new JsonMessageConverter();
    }

    @Bean
    public NewTopic statisticTrips() {
        return new NewTopic("StatisticTrips", 1, (short) 1);
    }

    @Bean
    public NewTopic scheduleCreated() {
        return new NewTopic("ScheduleCreated", 1, (short) 1);
    }
}
