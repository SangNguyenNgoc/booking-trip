package org.tripservice.trip.api.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trips")
public class Trip {
    private String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<String> seatsReserved;
    private Integer seatsAvailable;
    private String licensePlate;
    private Long price;
    private String scheduleId;
}
