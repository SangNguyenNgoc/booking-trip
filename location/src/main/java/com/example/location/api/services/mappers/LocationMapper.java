package com.example.location.api.services.mappers;

import com.example.location.api.dtos.location.LocationCreate;
import com.example.location.api.dtos.location.LocationInfo;
import com.example.location.api.dtos.location.LocationName;
import com.example.location.api.entities.Location;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {RegionMapper.class})
public interface LocationMapper {

    LocationInfo toDto(Location location);

    Location toEntity(LocationCreate locationCreate);

    LocationName toName(Location location);
}
