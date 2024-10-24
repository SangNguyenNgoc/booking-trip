package org.tripservice.trip.api.services.interfaces;

import org.tripservice.trip.api.dtos.schedule.ScheduleDetail;
import org.tripservice.trip.api.dtos.schedule.ScheduleRequest;
import org.tripservice.trip.api.dtos.schedule.ScheduleResponse;
import org.tripservice.trip.utils.dtos.ListResponse;

public interface ScheduleService {

    ScheduleDetail createSchedule(ScheduleRequest request);

    ListResponse<ScheduleResponse> getSchedulesByFromAndTo(String from, String to);


    ScheduleDetail updateSchedule(String id, ScheduleRequest request);


}
