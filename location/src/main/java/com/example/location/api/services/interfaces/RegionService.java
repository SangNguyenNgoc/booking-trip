package com.example.location.api.services.interfaces;

import com.example.location.api.dtos.region.RegionInfo;
import com.example.location.utils.dtos.ListResponse;

public interface RegionService {
    ListResponse<RegionInfo> getAllRegions();

    RegionInfo getRegionBySlug(String slug);
}
