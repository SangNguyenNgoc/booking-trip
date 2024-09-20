package com.example.location.api.dtos.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.example.location.api.entities.Location}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationName implements Serializable {
    private String name;
    private String address;
    private String slug;
}