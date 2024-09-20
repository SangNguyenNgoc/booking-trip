package com.example.location.api.dtos.location;

import com.example.location.api.dtos.region.RegionInfo;
import com.example.location.api.entities.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link Location}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationInfo implements Serializable {
    private Long id;
    private String name;
    private String address;
    private String slug;
    private String phoneNumber;
    private String description;
    private RegionInfo region;
}