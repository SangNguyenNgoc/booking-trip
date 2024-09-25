package com.example.location.api.services.interfaces;

import com.example.location.api.dtos.location.LocationCreate;
import com.example.location.api.dtos.location.LocationInfo;
import com.example.location.api.dtos.location.LocationName;
import com.example.location.api.dtos.location.LocationUpdate;
import com.example.location.utils.dtos.ListResponse;
import com.example.location.utils.dtos.PageResponse;

public interface LocationService {

    LocationInfo createLocation(LocationCreate locationCreate);

    PageResponse<LocationInfo> getALlLocations(Integer pageNo, Integer pageSize);

    LocationInfo getLocationBySlug(String slug);

    LocationInfo getLocationById(Long locationId);

    ListResponse<LocationName> getLocationNames();

    LocationInfo updateLocation(LocationUpdate locationUpdate);

    LocationInfo updateRegionInLocation(Long locationId, String regionSlug);

    void toggleActiveLocation(Long locationId);
}
