package com.example.location.api.controllers;

import com.example.location.LocationApplication;
import com.example.location.api.dtos.region.RegionInfo;
import com.example.location.api.entities.Region;
import com.example.location.api.services.interfaces.RegionService;
import com.example.location.utils.dtos.ListResponse;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = RegionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    protected RegionService regionService;

    @Setter
    private List<Region> regions;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Region>> typeReference = new TypeReference<List<Region>>() {};
        InputStream inputStream = LocationApplication.class.getResourceAsStream("/data/regions.json");
        try {
            List<Region> regions = mapper.readValue(inputStream, typeReference);
            setRegions(regions);
            System.out.println("Đã import " + regions.size() + " tỉnh/thành phố thành công.");
        } catch (Exception e) {
            System.out.println("Không thể import dữ liệu: " + e.getMessage());
        }
    }

    @Test
    void getAllRegions_ShouldReturnRegions() throws Exception {
        // Giả lập dữ liệu trả về
        List<RegionInfo> regions = Collections.singletonList(RegionInfo.builder()
                .slug("valid-slug")
                .name("Valid Region")
                .build());
        ListResponse<RegionInfo> response = new ListResponse<>(regions.size(), regions);

        when(regionService.getAllRegions()).thenReturn(response);

        // Gửi yêu cầu GET đến endpoint và kiểm tra phản hồi
        mockMvc.perform(get("/regions")) // Sử dụng URL động
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(regions.size()))
                .andExpect(jsonPath("$.data[0].slug").value("valid-slug"))
                .andExpect(jsonPath("$.data[0].name").value("Valid Region"));

        // Kiểm tra xem phương thức service đã được gọi
        verify(regionService).getAllRegions();
    }

    @Test
    void getRegionBySlug_ShouldReturnRegion() throws Exception {
        String slug = "valid-slug";
        RegionInfo regionInfo = RegionInfo.builder()
                .slug(slug)
                .name("Valid Region")
                .build();

        when(regionService.getRegionBySlug(slug)).thenReturn(regionInfo);

        // Gửi yêu cầu GET đến endpoint và kiểm tra phản hồi
        mockMvc.perform(get("/regions/{slug}", slug)) // Sử dụng URL động
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(slug))
                .andExpect(jsonPath("$.name").value("Valid Region"));

        // Kiểm tra xem phương thức service đã được gọi
        verify(regionService).getRegionBySlug(slug);
    }
}