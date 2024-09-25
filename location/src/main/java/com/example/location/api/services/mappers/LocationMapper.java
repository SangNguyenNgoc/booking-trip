package com.example.location.api.services.mappers;

import com.example.location.api.dtos.location.LocationCreate;
import com.example.location.api.dtos.location.LocationInfo;
import com.example.location.api.dtos.location.LocationName;
import com.example.location.api.dtos.location.LocationUpdate;
import com.example.location.api.entities.Location;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring", uses = {RegionMapper.class})
public interface LocationMapper {

    LocationInfo toDto(Location location);

    Location toEntity(LocationCreate locationCreate);

    LocationName toName(Location location);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Location partialUpdate(LocationUpdate locationUpdate, @MappingTarget Location location);
}
