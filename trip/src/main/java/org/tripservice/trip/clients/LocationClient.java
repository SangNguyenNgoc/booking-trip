package org.tripservice.trip.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.tripservice.trip.api.documents.Schedule;
import org.tripservice.trip.api.dtos.schedule.ScheduleRequest;
import org.tripservice.trip.config.ClientConfig;

import java.util.Optional;

@FeignClient(
        name = "location",
        configuration = ClientConfig.class
)
public interface LocationClient {

    @GetMapping("/internal/locations/schedule")
    Optional<Schedule> getTripSchedule(
            @RequestBody ScheduleRequest request,
            @RequestHeader("X-API-KEY") String apiKey
    );

}
