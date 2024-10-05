package com.example.location.api.entities;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "distances")
public class Distance {

    private String id;
    private String from;
    private String to;
    private Double distance; //(km)
    private Double duration; //(minus)
    private Double baseDuration; //(minus)
}
