package org.example.statistics.api.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "schedules")
public class Schedule {
    private String id;
    private String regionFrom;
    private String regionTo;
    private String from;
    private String to;
    private Long price;
}
