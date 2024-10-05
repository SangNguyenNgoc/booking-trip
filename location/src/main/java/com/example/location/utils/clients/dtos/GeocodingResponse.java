package com.example.location.utils.clients.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeocodingResponse {

    @JsonProperty("items")
    private List<Item> items;

    @Setter
    @Getter
    public static class Item {

        @JsonProperty("position")
        private Position position;

    }

    @Setter
    @Getter
    public static class Position {
        @JsonProperty("lat")
        private Double lat;

        @JsonProperty("lng")
        private Double lng;

    }
}

