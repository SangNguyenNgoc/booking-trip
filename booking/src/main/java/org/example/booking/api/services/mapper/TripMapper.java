package org.example.booking.api.services.mapper;

import org.example.booking.api.entities.Trip;
import org.example.booking.clients.dtos.TripResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TripMapper {
    @Mapping(source = "schedule.price", target = "price")
    Trip toEntity(TripResponse tripResponse);

}
