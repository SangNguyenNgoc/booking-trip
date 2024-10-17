package org.tripservice.trip.api.services.interfaces;

import org.tripservice.trip.api.documents.Schedule;
import org.tripservice.trip.api.dtos.schedule.TripScheduleRequest;
import org.tripservice.trip.utils.dtos.ListResponse;

import java.util.List;

public interface ScheduleService {

    Schedule createSchedule(TripScheduleRequest request);

    ListResponse<Schedule> getSchedulesByFromAndTo(String from, String to);

    Schedule updateSchedule(String id, TripScheduleRequest request);



}
