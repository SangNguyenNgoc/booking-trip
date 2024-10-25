package org.example.booking.api.services.mapper;

import org.example.booking.api.entities.Trip;
import org.example.booking.clients.dtos.TripResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TripMapper {
    @Mapping(source = "price", target = "price")
    @Mapping(source = "schedule.from.name", target = "locationFromName")
    @Mapping(source = "schedule.to.name", target = "locationToName")
    @Mapping(source = "schedule.regionTo.name", target = "regionToName")
    @Mapping(source = "schedule.regionFrom.name", target = "regionFromName")
    Trip toEntity(TripResponse tripResponse);

}
