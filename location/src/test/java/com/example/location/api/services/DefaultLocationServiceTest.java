package com.example.location.api.services;

import com.example.location.LocationApplication;
import com.example.location.api.dtos.location.*;
import com.example.location.api.entities.Location;
import com.example.location.api.entities.Region;
import com.example.location.api.repositories.DistanceRepository;
import com.example.location.api.repositories.LocationRepository;
import com.example.location.api.repositories.RegionRepository;
import com.example.location.api.services.mappers.LocationMapper;
import com.example.location.api.services.mappers.RegionMapper;
import com.example.location.clients.GeocodingClient;
import com.example.location.clients.RoutingClient;
import com.example.location.clients.dtos.GeocodingResponse;
import com.example.location.config.VariableConfig;
import com.example.location.utils.exception.DataNotFoundException;
import com.example.location.utils.exception.InputInvalidException;
import com.example.location.utils.services.AppUtils;
import com.example.location.utils.services.ObjectsValidator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@ExtendWith(MockitoExtension.class)
class DefaultLocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private DistanceRepository distanceRepository;

    @Mock
    private AppUtils appUtils;

    @Mock
    private GeocodingClient geocodingClient;

    @Mock
    private RoutingClient routingClient;

    @Spy
    @InjectMocks
    private LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);

    @Spy
    private RegionMapper regionMapper = Mappers.getMapper(RegionMapper.class);

    @Spy
    private ObjectsValidator<LocationCreate> validator = new ObjectsValidator<>();

    @Spy
    private VariableConfig variableConfig = new VariableConfig("api-key");

    @InjectMocks
    private DefaultLocationService locationService;

    @Setter
    private List<Location> locations;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Location>> typeReference = new TypeReference<>() {};
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
        locationCreate.setHotline("0912345678");
        locationCreate.setAddress("Address");
        locationCreate.setDescription("Description");

        var region = new Region();
        region.setSlug("valid-slug");

        var location = new Location();
        location.setName("New Location");
        location.setHotline("0912345678");

        // Dữ liệu giả cho geocoding result
        var geocodingResult = new GeocodingResponse();
        geocodingResult.setItems(List.of(new GeocodingResponse.Item()));
        geocodingResult.getItems().get(0).setPosition(new GeocodingResponse.Position());
        geocodingResult.getItems().get(0).getPosition().setLat(10.1234);
        geocodingResult.getItems().get(0).getPosition().setLng(106.5678);

        // Mock các repository, utils và client
        when(regionRepository.findBySlug("valid-slug")).thenReturn(Optional.of(region));
        when(locationRepository.existsByHotline("0912345678")).thenReturn(false);
        when(appUtils.toSlug("New Location")).thenReturn("new-location");
        when(locationRepository.save(Mockito.any(Location.class))).thenReturn(location);

        // Mock geocoding client không gọi API thực
        when(geocodingClient.getCoordinates(eq("Address"), anyString()))
                .thenReturn(new GeocodingResponse(List.of(geocodingResult.getItems().toArray(new GeocodingResponse.Item[0]))));

        LocationInfo result = locationService.createLocation(locationCreate);

        // Kiểm tra kết quả
        assertNotNull(result);

        // Verify các phương thức được gọi đúng cách
        verify(validator).validate(locationCreate);
        verify(regionRepository).findBySlug("valid-slug");
        verify(locationRepository).existsByHotline("0912345678");
        verify(geocodingClient).getCoordinates(eq("Address"), anyString());
        verify(locationRepository).save(Mockito.any(Location.class));
    }

    @Test
    void testCreateLocation_RegionNotFound() {
        when(regionRepository.findBySlug("invalid-slug")).thenReturn(Optional.empty());
        var locationCreate = new LocationCreate();
        locationCreate.setName("New Location");
        locationCreate.setHotline("0912345678");
        locationCreate.setAddress("Address");
        locationCreate.setDescription("Description");
        locationCreate.setRegionSlug("invalid-slug");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> locationService.createLocation(locationCreate));

        assertTrue(exception.getMessages().contains("Region not found"));

        verify(regionRepository).findBySlug("invalid-slug");
        verify(locationRepository, never()).existsByHotline(anyString());
        verify(locationRepository, never()).save(any());
    }

    @Test
    void testCreateLocation_PhoneNumberAlreadyExists() {

        var region = new Region();
        region.setSlug("valid-slug");

        var locationCreate = new LocationCreate();
        locationCreate.setName("New Location");
        locationCreate.setHotline("0912345678");
        locationCreate.setAddress("Address");
        locationCreate.setDescription("Description");
        locationCreate.setRegionSlug("valid-slug");

        when(regionRepository.findBySlug("valid-slug")).thenReturn(Optional.of(region));
        when(locationRepository.existsByHotline("0912345678")).thenReturn(true);

        InputInvalidException exception = assertThrows(InputInvalidException.class, () -> locationService.createLocation(locationCreate));

        assertTrue(exception.getMessages().contains("Phone number already exists"));

        verify(validator).validate(locationCreate);
        verify(locationRepository).existsByHotline("0912345678");
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
    void getALlLocationsUnderPage() {
        when(locationRepository.findAll()).thenReturn(locations);

        var result = locationService.getAllLocations();

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
    void getLocationBySlug_SlugNotFound() {
        var testSlug = "ben-xe-mien-dong";
        when(locationRepository.findBySlug(testSlug)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> locationService.getLocationBySlug(testSlug));
        assertNotNull(exception.getMessages());
        assertTrue(exception.getMessages().get(0).contains("Location not found"));
    }

    @Test
    void getLocationById() {
        var testId = "ben-xe-mien-dong";
        var testLocation = locations.get(1);
        when(locationRepository.findById(testId)).thenReturn(Optional.of(testLocation));

        var result = locationService.getLocationById(testId);

        assertNotNull(result);
        assertEquals(result.getName(), testLocation.getName());
    }

    @Test
    void getLocationById_IdNotFound() {
        var testId = "ben-xe-mien-dong";
        when(locationRepository.findById(testId)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> locationService.getLocationById(testId));

        assertNotNull(exception.getMessages());
        assertTrue(exception.getMessages().get(0).contains("Location not found"));
    }

    @Test
    void getLocationByRegionUnderPage() {
        var testRegion = "ho-chi-minh";
        var testLocation = locations;
        when(locationRepository.findByRegionId(testRegion)).thenReturn(testLocation);

        var result = locationService.getLocationByRegion(testRegion);
        assertNotNull(result);
        assertEquals(result.getData().getSize(), testLocation.size());
    }

    @Test
    void testGetLocationByRegion() {
        var testRegion = "ho-chi-minh";
        var testLocation = locations;
        when(locationRepository.findByRegionId(eq(testRegion), any(PageRequest.class))).thenReturn(new PageImpl<>(testLocation));

        var result = locationService.getLocationByRegion(testRegion, 1, 4);
        assertNotNull(result);
        assertEquals(result.getData().getSize(), testLocation.size());
    }

    @Test
    void testGetLocationNames() {
        when(locationRepository.findAllLocationNames()).thenReturn(locations);
        var result = locationService.getLocationNames();
        assertNotNull(result);
        assertEquals(result.getSize(), locations.size());
    }

    @Test
    void getLocationNameBySlug() {
        var testSlug = "ben-xe-mien-tay";
        var testLocation = locations.get(1);
        when(locationRepository.findBySlug(testSlug)).thenReturn(Optional.of(testLocation));

        var result = locationService.getLocationNameBySlug(testSlug);

        assertNotNull(result);
        assertEquals(result.getName(), testLocation.getName());

    }

    @Test
    void getLocationNameBySlug_SlugNotFound() {
        var testSlug = "ben-xe-mien-tay";
        when(locationRepository.findBySlug(testSlug)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> locationService.getLocationNameBySlug(testSlug));

        assertNotNull(exception.getMessages());
        assertTrue(exception.getMessages().get(0).contains("Location not found"));
    }

    @Test
    void updateLocation() {
        // Tạo dữ liệu giả cho LocationUpdate
        var locationUpdate = new LocationUpdate();
        locationUpdate.setId("1");
        locationUpdate.setRegionSlug("new-region-slug");
        locationUpdate.setName("Name");
        locationUpdate.setDescription("Description");
        locationUpdate.setHotline("0912345678");
        locationUpdate.setAddress("address");

        // Tạo đối tượng giả cho Location và Region
        var oldLocation = new Location();
        oldLocation.setId("1");
        oldLocation.setHotline("0987654321");
        var oldRegion = new Region();
        oldRegion.setSlug("old-region-slug");
        oldLocation.setRegion(oldRegion);

        var newRegion = new Region();
        newRegion.setSlug("new-region-slug");

        var updatedLocation = new Location();
        updatedLocation.setId("1");
        updatedLocation.setHotline("0912345678");
        updatedLocation.setRegion(newRegion);

        // Tạo đối tượng giả cho LocationInfo (DTO)
        var locationInfo = new LocationInfo();
        locationInfo.setId("1");

        // Mô phỏng hành vi của repository và mapper
        when(locationRepository.findById("1")).thenReturn(Optional.of(oldLocation));
        when(regionRepository.findBySlug("new-region-slug")).thenReturn(Optional.of(newRegion));
        when(locationRepository.existsByHotline("0912345678")).thenReturn(false);
        when(locationRepository.save(any())).thenReturn(updatedLocation);

        // Gọi hàm cập nhật
        LocationInfo result = locationService.updateLocation(locationUpdate);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("1", result.getId());

        // Kiểm tra xem các phương thức đã được gọi chính xác
        verify(locationRepository).findById("1");
        verify(regionRepository).findBySlug("new-region-slug");
        verify(locationRepository).existsByHotline("0912345678");
        verify(locationRepository).save(any());
    }

    @Test
    void updateLocation_LocationNotFound() {
        var locationUpdate = new LocationUpdate();
        locationUpdate.setId("1");
        locationUpdate.setRegionSlug("new-region-slug");
        locationUpdate.setName("Name");
        locationUpdate.setDescription("Description");
        locationUpdate.setHotline("0912345678");
        locationUpdate.setAddress("address");

        when(locationRepository.findById("1")).thenReturn(Optional.empty());

        // Kiểm tra xem có ném ra ngoại lệ `DataNotFoundException`
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> locationService.updateLocation(locationUpdate));

        assertTrue(exception.getMessages().get(0).contains("Location not found"));

        verify(locationRepository).findById("1");
        verify(regionRepository, never()).findBySlug(anyString());
        verify(locationRepository, never()).existsByHotline(anyString());
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void updateLocation_RegionNotFound() {
        var locationUpdate = new LocationUpdate();
        locationUpdate.setId("1");
        locationUpdate.setRegionSlug("new-region-slug");
        locationUpdate.setName("Name");
        locationUpdate.setDescription("Description");
        locationUpdate.setHotline("0912345678");
        locationUpdate.setAddress("address");

        var oldLocation = new Location();
        oldLocation.setId("1");
        var oldRegion = new Region();
        oldRegion.setSlug("old-region-slug");
        oldLocation.setRegion(oldRegion);

        when(locationRepository.findById("1")).thenReturn(Optional.of(oldLocation));
        when(regionRepository.findBySlug("new-region-slug")).thenReturn(Optional.empty());

        // Kiểm tra xem có ném ra ngoại lệ `DataNotFoundException`
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> locationService.updateLocation(locationUpdate));

        assertTrue(exception.getMessages().get(0).contains("Region not found"));

        verify(locationRepository).findById("1");
        verify(regionRepository).findBySlug("new-region-slug");
        verify(locationRepository, never()).existsByHotline(anyString());
        verify(locationRepository, never()).save(any(Location.class));
    }


    @Test
    void updateLocation_HotlineAlreadyExists() {
        var locationUpdate = new LocationUpdate();
        locationUpdate.setId("1");
        locationUpdate.setRegionSlug("new-region-slug");
        locationUpdate.setName("Name");
        locationUpdate.setDescription("Description");
        locationUpdate.setHotline("0912345678");
        locationUpdate.setAddress("address");

        var newRegion = new Region();
        newRegion.setSlug("new-region-slug");

        var oldLocation = new Location();
        oldLocation.setId("1");
        oldLocation.setHotline("0987654321");
        oldLocation.setRegion(newRegion);


        when(locationRepository.findById("1")).thenReturn(Optional.of(oldLocation));
        when(locationRepository.existsByHotline("0912345678")).thenReturn(true);

        // Kiểm tra xem có ném ra ngoại lệ `InputInvalidException`
        InputInvalidException exception = assertThrows(InputInvalidException.class,
                () -> locationService.updateLocation(locationUpdate));

        assertTrue(exception.getMessages().get(0).contains("Phone number already exists"));


        verify(locationRepository).findById("1");
        verify(locationRepository).existsByHotline("0912345678");
        verify(locationRepository, never()).save(any(Location.class));
    }



    @Test
    void toggleActiveLocation() {
        var testId = "ben-xe-mien-dong";
        var testLocation = locations.get(1);
        testLocation.setActive(true);
        when(locationRepository.findById(testId)).thenReturn(Optional.of(testLocation));

        locationService.toggleActiveLocation(testId);

        verify(locationRepository).findById(testId);
        assertFalse(testLocation.getActive());
    }

    @Test
    void toggleActiveLocation_IdNotFound() {
        var testId = "ben-xe-mien-dong";
        when(locationRepository.findById(testId)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> locationService.toggleActiveLocation(testId));

        assertNotNull(exception.getMessages());
        assertTrue(exception.getMessages().get(0).contains("Location not found"));

    }

}