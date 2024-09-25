package com.example.location.api.services;

import com.example.location.LocationApplication;
import com.example.location.api.dtos.location.LocationCreate;
import com.example.location.api.dtos.location.LocationInfo;
import com.example.location.api.entities.Location;
import com.example.location.api.entities.Region;
import com.example.location.api.repositories.LocationRepository;
import com.example.location.api.repositories.RegionRepository;
import com.example.location.api.services.mappers.LocationMapper;
import com.example.location.api.services.mappers.RegionMapper;
import com.example.location.utils.exception.DataNotFoundException;
import com.example.location.utils.exception.InputInvalidException;
import com.example.location.utils.services.AppUtils;
import com.example.location.utils.services.ObjectsValidator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultLocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private AppUtils appUtils;

    @Spy
    @InjectMocks
    private LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);

    @Spy
    private RegionMapper regionMapper = Mappers.getMapper(RegionMapper.class);

    @Spy
    private ObjectsValidator<LocationCreate> validator = new ObjectsValidator<>();

    @InjectMocks
    private DefaultLocationService locationService;

    @Setter
    private List<Location> locations;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Location>> typeReference = new TypeReference<List<Location>>() {};
        InputStream inputStream = LocationApplication.class.getResourceAsStream("/data/locations.json");
        try {
            List<Location> locations = mapper.readValue(inputStream, typeReference);
            setLocations(locations);
            System.out.println("Đã import " + locations.size() + " tỉnh/thành phố thành công.");
        } catch (Exception e) {
            System.out.println("Không thể import dữ liệu: " + e.getMessage());
        }
    }

    @Test
    void createLocation() {
        // Tạo các đối tượng giả
        var locationCreate = new LocationCreate();
        locationCreate.setName("New Location");
        locationCreate.setRegionSlug("valid-slug");
        locationCreate.setPhoneNumber("0912345678");
        locationCreate.setAddress("Address");
        locationCreate.setDescription("Description");

        var region = new Region();
        region.setSlug("valid-slug");

        var location = new Location();
        location.setName("New Location");
        location.setPhoneNumber("0912345678");

        when(regionRepository.findBySlug("valid-slug")).thenReturn(Optional.of(region));
        when(locationRepository.existsByPhoneNumber("0912345678")).thenReturn(false);
        when(appUtils.toSlug("New Location")).thenReturn("new-location");
        when(locationRepository.save(Mockito.any(Location.class))).thenReturn(location);

        LocationInfo result = locationService.createLocation(locationCreate);

        assertNotNull(result);

        verify(validator).validate(locationCreate);
        verify(regionRepository).findBySlug("valid-slug");
        verify(locationRepository).existsByPhoneNumber("0912345678");
        verify(locationRepository).save(Mockito.any(Location.class));
    }

    @Test
    void testCreateLocation_RegionNotFound() {
        when(regionRepository.findBySlug("invalid-slug")).thenReturn(Optional.empty());
        var locationCreate = new LocationCreate();
        locationCreate.setName("New Location");
        locationCreate.setPhoneNumber("0912345678");
        locationCreate.setAddress("Address");
        locationCreate.setDescription("Description");
        locationCreate.setRegionSlug("invalid-slug");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> {
            locationService.createLocation(locationCreate);
        });

        assertTrue(exception.getMessages().contains("Region not found"));

        verify(regionRepository).findBySlug("invalid-slug");
        verify(locationRepository, never()).existsByPhoneNumber(anyString());
        verify(locationRepository, never()).save(any());
    }

    @Test
    void testCreateLocation_PhoneNumberAlreadyExists() {

        var region = new Region();
        region.setSlug("valid-slug");

        var locationCreate = new LocationCreate();
        locationCreate.setName("New Location");
        locationCreate.setPhoneNumber("0912345678");
        locationCreate.setAddress("Address");
        locationCreate.setDescription("Description");
        locationCreate.setRegionSlug("valid-slug");

        when(regionRepository.findBySlug("valid-slug")).thenReturn(Optional.of(region));
        when(locationRepository.existsByPhoneNumber("0912345678")).thenReturn(true);

        InputInvalidException exception = assertThrows(InputInvalidException.class, () -> {
            locationService.createLocation(locationCreate);
        });

        assertTrue(exception.getMessages().contains("Phone number already exists"));

        verify(validator).validate(locationCreate);
        verify(locationRepository).existsByPhoneNumber("0912345678");
        verify(locationRepository, never()).save(any());
    }

    @Test
    void getALlLocations() {
        when(locationRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(locations));

        var result = locationService.getALlLocations(1, 4);

        assertNotNull(result);
        assertEquals(locations.size(), result.getData().getSize());
    }

    @Test
    void getLocationBySlug() {
        var testSlug = "ben-xe-mien-dong";
        var testLocation = locations.get(1);
        when(locationRepository.findBySlug(testSlug)).thenReturn(Optional.of(testLocation));

        var result = locationService.getLocationBySlug(testSlug);

        assertNotNull(result);
        assertEquals(result.getName(), testLocation.getName());
    }

    @Test
    void getLocationNames() {
        when(locationRepository.getAllLocationNames()).thenReturn(locations);

        var result = locationService.getLocationNames();

        assertNotNull(result);
        assertEquals(locations.size(), result.getSize());
    }
}