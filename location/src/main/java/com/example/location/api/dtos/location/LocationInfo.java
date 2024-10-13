package com.example.location.api.dtos.location;

import com.example.location.api.dtos.region.RegionInfo;
import com.example.location.api.entities.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link Location}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationInfo implements Serializable {
    private String id;
    private String name;
    private String address;
    private String slug;
    private String hotline;
    private String description;
    private Double latitude;
    private Double longitude;
    private Boolean active;
    private RegionInfo region;

}