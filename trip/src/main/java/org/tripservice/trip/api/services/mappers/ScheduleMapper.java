package org.tripservice.trip.api.services.mappers;

import org.mapstruct.Mapper;
import org.tripservice.trip.api.documents.Schedule;
import org.tripservice.trip.api.dtos.schedule.ScheduleDetail;
import org.tripservice.trip.api.dtos.schedule.ScheduleResponse;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    ScheduleResponse toResponse(Schedule schedule);

    ScheduleDetail toDetail(Schedule schedule);
}
