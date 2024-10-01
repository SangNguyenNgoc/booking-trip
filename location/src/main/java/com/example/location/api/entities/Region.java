package com.example.location.api.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "regions")
public class Region {

    @Id
    private String id;
    private String name;
    private String slug;
    private String type;
    private String nameWithType;
    private Integer code;

}
