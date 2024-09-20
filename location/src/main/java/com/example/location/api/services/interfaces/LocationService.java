package com.example.location.api.services.interfaces;

import com.example.location.api.dtos.location.LocationCreate;
import com.example.location.api.dtos.location.LocationInfo;
import com.example.location.api.dtos.location.LocationName;
import com.example.location.utils.dtos.ListResponse;

public interface LocationService {

    LocationInfo createLocation(LocationCreate locationCreate);

    ListResponse<LocationInfo> getALlLocations();

    LocationInfo getLocationBySlug(String slug);

    ListResponse<LocationName> getLocationNames();
}
