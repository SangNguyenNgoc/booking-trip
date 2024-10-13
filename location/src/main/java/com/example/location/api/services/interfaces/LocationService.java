package com.example.location.api.services.interfaces;

import com.example.location.api.dtos.location.*;
import com.example.location.api.entities.Distance;
import com.example.location.api.entities.Location;
import com.example.location.utils.dtos.ListResponse;
import com.example.location.utils.dtos.PageResponse;

import java.util.List;

public interface LocationService {

    LocationInfo createLocation(LocationCreate locationCreate);

    PageResponse<LocationInfo> getAllLocations();

    PageResponse<LocationInfo> getALlLocations(Integer pageNo, Integer pageSize);

    LocationInfo getLocationBySlug(String slug);

    PageResponse<LocationInfo> getLocationByRegion(String region);

    PageResponse<LocationInfo> getLocationByRegion(String region, Integer pageNo, Integer pageSize);

    LocationInfo getLocationById(String locationId);

    ListResponse<LocationName> getLocationNames();

    LocationName getLocationNameBySlug(String slug);

    LocationInfo updateLocation(LocationUpdate locationUpdate);

    void toggleActiveLocation(String locationId);

    TripScheduleResponse getTripSchedule(TripScheduleRequest request);

    Distance calculateAndSetDistance(Location from, Location to);

    List<ScheduleInfo> calculateAndSetScheduleInfo(Location to, List<Location> from, boolean inSchedule);

}
