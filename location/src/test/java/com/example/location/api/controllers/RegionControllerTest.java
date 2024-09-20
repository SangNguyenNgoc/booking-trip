package com.example.location.api.controllers;

import com.example.location.LocationApplication;
import com.example.location.api.entities.Region;
import com.example.location.api.services.interfaces.RegionService;
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
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = RegionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
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
    void getAllRegions() {
    }

    @Test
    void getRegionBySlug() {
    }
}