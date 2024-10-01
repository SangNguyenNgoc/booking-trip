package com.example.location.api.entities;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "locations")
public class Location {

    private String id;
    private String name;
    private String slug;
    private String type;
    private String address;
    private String hotline;
    private String description;
    private String latitude;
    private String longitude;
    private Boolean active;

    @DBRef
    private Region region;
}
