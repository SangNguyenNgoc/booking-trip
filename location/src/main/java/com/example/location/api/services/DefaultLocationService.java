package com.example.location.api.services;

import com.example.location.api.dtos.location.*;
import com.example.location.api.entities.Distance;
import com.example.location.api.entities.Location;
import com.example.location.api.repositories.DistanceRepository;
import com.example.location.api.repositories.LocationRepository;
import com.example.location.api.repositories.RegionRepository;
import com.example.location.api.services.interfaces.LocationService;
import com.example.location.api.services.mappers.LocationMapper;
import com.example.location.config.VariableConfig;
import com.example.location.utils.clients.GeocodingClient;
import com.example.location.utils.clients.RoutingClient;
import com.example.location.utils.dtos.ListResponse;
import com.example.location.utils.dtos.PageResponse;
import com.example.location.utils.exception.DataNotFoundException;
import com.example.location.utils.exception.InputInvalidException;
import com.example.location.utils.services.AppUtils;
import com.example.location.utils.services.ObjectsValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DefaultLocationService implements LocationService {

    LocationRepository locationRepository;
    RegionRepository regionRepository;
    DistanceRepository distanceRepository;

    LocationMapper locationMapper;

    ObjectsValidator<LocationCreate> locationValidator;
    ObjectsValidator<LocationUpdate> locationUpdateValidator;

    GeocodingClient geocodingClient;
    RoutingClient routingClient;

    AppUtils appUtils;
    VariableConfig variable;

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
        var geocodingResult = geocodingClient.getCoordinates(locationCreate.getAddress(), variable.LOCATION_API_KEY).getItems().get(0);
        location.setLatitude(geocodingResult.getPosition().getLat());
        location.setLongitude(geocodingResult.getPosition().getLng());

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

    public Location getBySlug(String slug) {
        return locationRepository.findBySlug(slug).orElseThrow(
                () -> new DataNotFoundException(List.of("Location not found"))
        );
    }

    @Override
    public TripScheduleResponse getTripSchedule(TripScheduleRequest request) {
        var from = getBySlug(request.getFrom());
        var to = getBySlug(request.getTo());
        var pickUpList = getLocationsBySlug(request.getPickUps());
        var transitList = getLocationsBySlug(request.getTransits());

        var response = buildTripScheduleResponse(from, to, pickUpList, transitList);

        Distance distance = calculateAndSetDistance(from, to);
        response.setDuration(distance.getDuration());
        response.setDistance(distance.getDistance());

        return response;
    }

    private TripScheduleResponse buildTripScheduleResponse(Location from, Location to,
                                                           List<Location> pickUpList, List<Location> transitList) {

        return TripScheduleResponse.builder()
                .from(locationMapper.toName(from))
                .to(locationMapper.toName(to))
                .pickUps(calculateAndSetScheduleInfo(from, pickUpList, false))
                .transits(calculateAndSetScheduleInfo(from, transitList, true))
                .build();
    }

    @Override
    public List<ScheduleInfo> calculateAndSetScheduleInfo(Location to, List<Location> from, boolean inSchedule) {
        return from.stream().map(location -> {
            var distance = calculateAndSetDistance(location, to);
            return ScheduleInfo.builder()
                    .name(location.getName())
                    .address(location.getAddress())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .slug(location.getSlug())
                    .distanceToLocation(distance.getDistance())
                    .durationToLocation(inSchedule ? distance.getDuration() : distance.getDuration()*(-1))
                    .build();
        }).collect(Collectors.toList());
    }

    private List<Location> getLocationsBySlug(List<String> slugs) {
        return locationRepository.findAllBySlugIn(slugs);
    }


    @Override
    public Distance calculateAndSetDistance(Location from, Location to) {

        return distanceRepository.findByFromAndToOrToAndFrom(from.getSlug(), to.getSlug())
                .orElseGet(() -> calculateAndSetNewDistance(from, to));

    }

    private Distance calculateAndSetNewDistance(Location from, Location to) {
        var route = routingClient.getRoute(
                "car",
                from.getLatitude() + "," + from.getLongitude(),
                to.getLatitude() + "," + to.getLongitude(),
                "summary",
                variable.LOCATION_API_KEY
        );

        var section = route.getRoutes().get(0).getSections().get(0).getSummary();
        Distance newDistance = Distance.builder()
                .from(from.getSlug())
                .to(to.getSlug())
                .distance(section.getLength().doubleValue() / 1000) // chuyển đổi sang km
                .baseDuration(section.getBaseDuration().doubleValue() / 60) // chuyển đổi sang phút
                .duration(section.getDuration().doubleValue() / 60) // chuyển đổi sang phút
                .build();

        return distanceRepository.save(newDistance);
    }


}
