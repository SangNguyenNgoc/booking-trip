package org.tripservice.trip.api.services.interfaces;

import org.tripservice.trip.api.documents.Schedule;
import org.tripservice.trip.api.dtos.schedule.TripScheduleRequest;

import java.util.List;

public interface ScheduleService {

    Schedule createSchedule(TripScheduleRequest request);

    List<Schedule> getSchedulesByFromAndTo(String from, String to);

    Schedule updateSchedule(String id, TripScheduleRequest request);



}
