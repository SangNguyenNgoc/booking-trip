package com.example.location.clients.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponse {
    private List<Route> routes;

    @Getter
    @Setter
    public static class Route {
        private List<Section> sections;

        @Getter
        @Setter
        public static class Section {
            private Summary summary;

            @Getter
            @Setter
            public static class Summary {
                private Integer length; //(m)
                private Integer duration; //(s)
                private Integer baseDuration; //(s)

            }
        }
    }
}
