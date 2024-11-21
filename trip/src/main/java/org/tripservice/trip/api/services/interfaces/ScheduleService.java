package org.tripservice.trip.api.services.interfaces;

import org.tripservice.trip.api.dtos.location.RegionAndSchedule;
import org.tripservice.trip.api.dtos.schedule.ScheduleDetail;
import org.tripservice.trip.api.dtos.schedule.ScheduleGroup;
import org.tripservice.trip.api.dtos.schedule.ScheduleRequest;
import org.tripservice.trip.api.dtos.schedule.ScheduleResponse;
import org.tripservice.trip.utils.dtos.ListResponse;

import java.util.List;

public interface ScheduleService {

    ScheduleDetail createSchedule(ScheduleRequest request);

    ListResponse<RegionAndSchedule> getSchedulesByFromAndToGrouping(String from, String to);

    ListResponse<ScheduleResponse> getSchedulesByFromAndTo(String from, String to);

    ScheduleDetail updateSchedule(String id, ScheduleRequest request);

    List<RegionAndSchedule> getPopularSchedule();

    ScheduleDetail getSchedule(String id);

    ListResponse<ScheduleGroup> getSchedulesByFromAndToGroupByLocation(String from, String to);
}
