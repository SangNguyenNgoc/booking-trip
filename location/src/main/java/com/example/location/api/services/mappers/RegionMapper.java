package com.example.location.api.services.mappers;

import com.example.location.api.dtos.region.RegionInfo;
import com.example.location.api.entities.Region;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RegionMapper {

    RegionInfo toDto(Region region);

}