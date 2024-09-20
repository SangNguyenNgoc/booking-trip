package com.example.location.api.services;

import com.example.location.api.dtos.location.LocationCreate;
import com.example.location.api.dtos.location.LocationInfo;
import com.example.location.api.dtos.location.LocationName;
import com.example.location.api.repositories.LocationRepository;
import com.example.location.api.repositories.RegionRepository;
import com.example.location.api.services.interfaces.LocationService;
import com.example.location.api.services.mappers.LocationMapper;
import com.example.location.utils.dtos.ListResponse;
import com.example.location.utils.exception.DataNotFoundException;
import com.example.location.utils.exception.InputInvalidException;
import com.example.location.utils.services.AppUtils;
import com.example.location.utils.services.ObjectsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultLocationService implements LocationService {

    private final LocationRepository locationRepository;
    private final RegionRepository regionRepository;
    private final LocationMapper locationMapper;
    private final ObjectsValidator<LocationCreate> locationValidator;
    private final AppUtils appUtils;

    @Override
    public LocationInfo createLocation(LocationCreate locationCreate) {
        locationValidator.validate(locationCreate);
        var region = regionRepository.findBySlug(locationCreate.getRegionSlug()).orElseThrow(
                () -> new DataNotFoundException(List.of("Region not found"))
        );
        if(locationRepository.existsByPhoneNumber(locationCreate.getPhoneNumber())) {
            throw new InputInvalidException(List.of("Phone number already exists"));
        }
        var location = locationMapper.toEntity(locationCreate);
        location.setSlug(appUtils.toSlug(locationCreate.getName()));
        location.setRegion(region);
        var result = locationRepository.save(location);
        return locationMapper.toDto(location);
    }

    @Override
    public ListResponse<LocationInfo> getALlLocations() {
        var locations = locationRepository.findAll();
        return ListResponse.<LocationInfo>builder()
                .size(locations.size())
                .data(locations.stream().map(locationMapper::toDto).collect(Collectors.toList()))
                .build();
    }

    @Override
    public LocationInfo getLocationBySlug(String slug) {
        var location = locationRepository.findBySlug(slug).orElseThrow(
                () -> new DataNotFoundException(List.of("Location not found"))
        );
        return locationMapper.toDto(location);
    }

    @Override
    public ListResponse<LocationName> getLocationNames() {
        var locationNames = locationRepository.getAllLocationNames();
        return ListResponse.<LocationName>builder()
                .size(locationNames.size())
                .data(locationNames.stream().map(locationMapper::toName).collect(Collectors.toList()))
                .build();
    }
}
