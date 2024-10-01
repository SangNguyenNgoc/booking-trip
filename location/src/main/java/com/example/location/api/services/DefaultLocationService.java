package com.example.location.api.services;

import com.example.location.api.dtos.location.LocationCreate;
import com.example.location.api.dtos.location.LocationInfo;
import com.example.location.api.dtos.location.LocationName;
import com.example.location.api.dtos.location.LocationUpdate;
import com.example.location.api.repositories.LocationRepository;
import com.example.location.api.repositories.RegionRepository;
import com.example.location.api.services.interfaces.LocationService;
import com.example.location.api.services.mappers.LocationMapper;
import com.example.location.utils.dtos.ListResponse;
import com.example.location.utils.dtos.PageResponse;
import com.example.location.utils.exception.DataNotFoundException;
import com.example.location.utils.exception.InputInvalidException;
import com.example.location.utils.services.AppUtils;
import com.example.location.utils.services.ObjectsValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultLocationService implements LocationService {

    private final LocationRepository locationRepository;
    private final RegionRepository regionRepository;
    private final LocationMapper locationMapper;
    private final ObjectsValidator<LocationCreate> locationValidator;
    private final ObjectsValidator<LocationUpdate> locationUpdateValidator;
    private final AppUtils appUtils;

    @Override
    public LocationInfo createLocation(LocationCreate locationCreate) {
        locationValidator.validate(locationCreate);
        var region = regionRepository.findBySlug(locationCreate.getRegionSlug()).orElseThrow(
                () -> new DataNotFoundException(List.of("Region not found"))
        );
        if(locationRepository.existsByHotline(locationCreate.getHotline())) {
            throw new InputInvalidException(List.of("Phone number already exists"));
        }
        var location = locationMapper.toEntity(locationCreate);
        location.setSlug(appUtils.toSlug(locationCreate.getName()));
        location.setType("bus_station");
        location.setActive(true);
        location.setRegion(region);
        var result = locationRepository.save(location);
        return locationMapper.toDto(result);
    }

    @Override
    public PageResponse<LocationInfo> getALlLocations(Integer pageNo, Integer pageSize) {
        PageRequest page = PageRequest.of(pageNo, pageSize);
        var locationPage = locationRepository.findAll(page);
        var locations = ListResponse.<LocationInfo>builder()
                .size(locationPage.getSize())
                .data(locationPage.getContent().stream().map(locationMapper::toDto).collect(Collectors.toList()))
                .build();
        return PageResponse.<LocationInfo>builder()
                .currentPage(pageNo + 1)
                .totalPage(locationPage.getTotalPages())
                .data(locations)
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
    public LocationInfo getLocationById(String locationId) {
        var location = locationRepository.findById(locationId).orElseThrow(
                () -> new DataNotFoundException(List.of("Location not found"))
        );
        return locationMapper.toDto(location);
    }

    @Override
    public PageResponse<LocationInfo> getLocationByRegion(String region, Integer pageNo, Integer pageSize) {
        PageRequest page = PageRequest.of(pageNo, pageSize);
        var locationPage = locationRepository.findByRegionId(region, page);
        var locations = ListResponse.<LocationInfo>builder()
                .size(locationPage.getSize())
                .data(locationPage.getContent().stream().map(locationMapper::toDto).collect(Collectors.toList()))
                .build();
        return PageResponse.<LocationInfo>builder()
                .currentPage(pageNo + 1)
                .totalPage(locationPage.getTotalPages())
                .data(locations)
                .build();
    }

    @Override
    public ListResponse<LocationName> getLocationNames() {
        var locationNames = locationRepository.getAllLocationNames();
        return ListResponse.<LocationName>builder()
                .size(locationNames.size())
                .data(locationNames.stream().map(locationMapper::toName).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public LocationInfo updateLocation(LocationUpdate locationUpdate) {
        locationUpdateValidator.validate(locationUpdate);
        var location = locationRepository.findById(locationUpdate.getId()).orElseThrow(
                () -> new DataNotFoundException(List.of("Location not found"))
        );
        if (!locationUpdate.getRegionSlug().equals(location.getRegion().getSlug())) {
            var region = regionRepository.findBySlug(locationUpdate.getRegionSlug()).orElseThrow(
                    () -> new DataNotFoundException(List.of("Region not found"))
            );
            location.setRegion(region);
        }
        if(!locationUpdate.getHotline().equals(location.getHotline())
                && locationRepository.existsByHotline(locationUpdate.getHotline())) {
            throw new InputInvalidException(List.of("Phone number already exists"));
        }
        var locationUpdated = locationMapper.partialUpdate(locationUpdate, location);
        locationRepository.save(locationUpdated);
        return locationMapper.toDto(locationUpdated);
    }

    @Override
    @Transactional
    public LocationInfo updateRegionInLocation(String locationId, String regionSlug) {
        var location = locationRepository.findById(locationId).orElseThrow(
                () -> new DataNotFoundException(List.of("Location not found"))
        );
        if (!regionSlug.equals(location.getRegion().getSlug())) {
            var region = regionRepository.findBySlug(regionSlug).orElseThrow(
                    () -> new DataNotFoundException(List.of("Region not found"))
            );
            location.setRegion(region);
            locationRepository.save(location);
        }
        return locationMapper.toDto(location);
    }

    @Override
    @Transactional
    public void toggleActiveLocation(String locationId) {
        var location = locationRepository.findById(locationId).orElseThrow(
                () -> new DataNotFoundException(List.of("Location not found"))
        );
        location.setActive(!location.getActive());
    }
}
