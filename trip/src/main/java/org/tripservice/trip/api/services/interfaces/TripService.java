package org.tripservice.trip.api.services.interfaces;

import org.tripservice.trip.api.documents.Schedule;
import org.tripservice.trip.api.documents.Trip;
import org.tripservice.trip.api.dtos.schedule.ScheduleResponse;
import org.tripservice.trip.api.dtos.trip.TripCreate;
import org.tripservice.trip.api.dtos.trip.TripDetail;
import org.tripservice.trip.api.dtos.trip.TripInfo;
import org.tripservice.trip.utils.dtos.ListResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TripService {

    List<TripInfo> createTrip(TripCreate tripCreate);

    List<Trip> scheduleTrips(Schedule schedule, List<String> vehicles, LocalDateTime start, LocalDateTime end);

    ListResponse<ScheduleResponse> getSchedulesIncludeTripsByFromAndTo(String from, String to, LocalDate date);

    TripDetail getTripDetail(String tripId);

    TripDetail getTripDetailForBooking(String tripId);

}
