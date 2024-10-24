package org.tripservice.trip.api.dtos.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegionInfo implements Serializable {
    private String id;
    private String name;
    private String slug;
    private String type;
    private String nameWithType;
    private Integer code;
}