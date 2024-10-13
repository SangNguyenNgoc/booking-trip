package com.example.location.api.controllers;

import com.example.location.LocationApplication;
import com.example.location.api.dtos.location.LocationCreate;
import com.example.location.api.dtos.location.LocationInfo;
import com.example.location.api.dtos.location.LocationName;
import com.example.location.api.dtos.location.LocationUpdate;
import com.example.location.api.entities.Location;
import com.example.location.api.services.interfaces.LocationService;
import com.example.location.utils.dtos.ListResponse;
import com.example.location.utils.dtos.PageResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LocationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @Setter
    List<Location> locations;

    ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
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
    void createLocation_ShouldReturnCreatedLocation() throws Exception {
        // Tạo đối tượng LocationCreate và LocationInfo
        LocationCreate locationCreate = LocationCreate.builder()
                .name("Test Location")
                .address("123 Test St.")
                .hotline("0123456789")
                .description("This is a test location.")
                .regionSlug("test-region")
                .build();

        LocationInfo locationInfo = LocationInfo.builder()
                .id("1")
                .name(locationCreate.getName())
                .address(locationCreate.getAddress())
                .slug("test-location")
                .hotline(locationCreate.getHotline())
                .description(locationCreate.getDescription())
                .latitude(10.0)
                .longitude(20.0)
                .active(true)
                .region(null) // Hoặc một đối tượng RegionInfo nếu cần thiết
                .build();

        // Mock hành vi của dịch vụ
        when(locationService.createLocation(locationCreate)).thenReturn(locationInfo);

        // Thực hiện yêu cầu POST
        mockMvc.perform(post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(locationCreate)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(locationInfo.getId()))
                .andExpect(jsonPath("$.name").value(locationInfo.getName()))
                .andExpect(jsonPath("$.address").value(locationInfo.getAddress()))
                .andExpect(jsonPath("$.hotline").value(locationInfo.getHotline()))
                .andExpect(jsonPath("$.description").value(locationInfo.getDescription()));

        // Xác minh rằng phương thức đã được gọi một lần
        verify(locationService, times(1)).createLocation(locationCreate);
    }


    @Test
    void getAllLocations_ShouldReturnAllLocations() throws Exception {
        // Tạo một đối tượng PageResponse giả định với một danh sách các LocationInfo
        PageResponse<LocationInfo> pageResponse = new PageResponse<>();
        ListResponse<LocationInfo> locations = ListResponse.<LocationInfo>builder()
                .data(List.of(
                        LocationInfo.builder().id("1").name("Location 1").build(),
                        LocationInfo.builder().id("2").name("Location 2").build()
                ))
                .build();
        pageResponse.setData(locations);
        pageResponse.setTotalPage(4);

        // Mock hành vi của dịch vụ
        when(locationService.getAllLocations()).thenReturn(pageResponse);

        // Thực hiện yêu cầu GET
        mockMvc.perform(get("/locations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalPage").value(pageResponse.getTotalPage()));

        // Xác minh rằng phương thức đã được gọi một lần
        verify(locationService, times(1)).getAllLocations();
    }


    @Test
    void getLocationBySlug_ShouldReturnLocation() throws Exception {
        String slug = "example-slug";
        LocationInfo locationInfo = LocationInfo.builder()
                .id("1")
                .name("Example Location")
                .address("456 Example Rd.")
                .slug(slug)
                .hotline("0987654321")
                .description("This is an example location.")
                .latitude(10.5)
                .longitude(20.5)
                .active(true)
                .region(null) // Hoặc một đối tượng RegionInfo nếu cần thiết
                .build();

        // Mock hành vi của dịch vụ
        when(locationService.getLocationBySlug(slug)).thenReturn(locationInfo);

        // Thực hiện yêu cầu GET
        mockMvc.perform(get("/locations/{slug}", slug))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug").value(slug))
                .andExpect(jsonPath("$.name").value(locationInfo.getName()))
                .andExpect(jsonPath("$.address").value(locationInfo.getAddress()));

        // Xác minh rằng phương thức đã được gọi một lần
        verify(locationService, times(1)).getLocationBySlug(slug);
    }


    @Test
    void testGetLocationByRegion_NoPageNoOrSize() throws Exception {
        String regionId = "region123";
        PageResponse<LocationInfo> pageResponse = new PageResponse<>();
        pageResponse.setData(ListResponse.<LocationInfo>builder()
                .data(new ArrayList<>())
                .build());

        when(locationService.getLocationByRegion(regionId)).thenReturn(pageResponse);

        mockMvc.perform(get("/locations/region/{regionId}", regionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data").isArray());
    }

    @Test
    void testGetLocationByRegion_WithPageNoAndSize() throws Exception {
        String regionId = "region123";
        int pageNo = 1;
        int pageSize = 10;
        PageResponse<LocationInfo> pageResponse = new PageResponse<>();
        pageResponse.setData(ListResponse.<LocationInfo>builder()
                .data(new ArrayList<>())
                .build());
        when(locationService.getLocationByRegion(regionId, pageNo - 1, pageSize)).thenReturn(pageResponse);

        mockMvc.perform(get("/locations/region/{regionId}", regionId)
                        .param("page", String.valueOf(pageNo))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data").isArray());
    }

    @Test
    void testGetAllLocationNames() throws Exception {
        ListResponse<LocationName> locationNames = new ListResponse<>();
        locationNames.setData(Collections.emptyList());

        when(locationService.getLocationNames()).thenReturn(locationNames);

        mockMvc.perform(get("/locations/names"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetLocationNameBySlug() throws Exception {
        String slug = "slug123";
        LocationName locationName = new LocationName();
        locationName.setName("Location Name");

        when(locationService.getLocationNameBySlug(slug)).thenReturn(locationName);

        mockMvc.perform(get("/locations/names/{slug}", slug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Location Name"));
    }

    @Test
    void updateLocation() throws Exception {
        LocationUpdate locationUpdate = new LocationUpdate();
        locationUpdate.setName("Updated Location");

        LocationInfo updatedLocation = new LocationInfo();
        updatedLocation.setName("Updated Location");

        when(locationService.updateLocation(any(LocationUpdate.class))).thenReturn(updatedLocation);

        mockMvc.perform(put("/locations").contentType("application/json")
                        .content("{\"name\": \"Updated Location\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Location"));
    }

    @Test
    void toggleActiveLocation() throws Exception {
        String locationId = "location123";

        doNothing().when(locationService).toggleActiveLocation(locationId);

        mockMvc.perform(put("/locations/{locationId}/active", locationId))
                .andExpect(status().isOk());
    }

    @Test
    void getTripSchedule() {
    }
}