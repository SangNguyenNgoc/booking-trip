package org.tripservice.trip.api.services.mappers;

import org.mapstruct.Mapper;
import org.tripservice.trip.api.documents.Trip;
import org.tripservice.trip.api.dtos.trip.TripDetail;
import org.tripservice.trip.api.dtos.trip.TripInfo;
import org.tripservice.trip.api.dtos.trip.TripStatistic;

@Mapper(componentModel = "spring")
public interface TripMapper {

    TripInfo toInfo(Trip trip);

    TripDetail toDetail(Trip trip);
}
