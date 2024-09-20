package com.example.location.api.services;

import com.example.location.LocationApplication;
import com.example.location.api.entities.Region;
import com.example.location.api.repositories.RegionRepository;
import com.example.location.api.services.mappers.RegionMapper;
import com.example.location.utils.exception.DataNotFoundException;
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

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefaultRegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @Spy
    private RegionMapper regionMapper = Mappers.getMapper(RegionMapper.class);

    @InjectMocks
    private DefaultRegionService regionService;

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
        Mockito.when(regionRepository.findAll()).thenReturn(regions);

        var result = regionService.getAllRegions();

        assertNotNull(result);
        assertEquals(regions.size(), result.getSize());
    }

    @Test
    void getRegionBySlug() {
        var testSlug = "binh-duong";
        var region = regions.stream().filter(item -> item.getSlug().equals(testSlug)).findFirst();
        Mockito.when(regionRepository.findBySlug(testSlug)).thenReturn(region);

        var result = regionService.getRegionBySlug(testSlug);
        assertNotNull(result);
        assertEquals("Bình Dương", result.getName());
        verify(regionRepository, times(1)).findBySlug(testSlug);
    }

    @Test
    void testGetRegionBySlug_RegionNotFound() {
        var testSlug = "binh-duong";
        var errorMessage = "Region not found";
        Mockito.when(regionRepository.findBySlug(testSlug)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> regionService.getRegionBySlug(testSlug)
        );

        assertEquals(errorMessage, exception.getMessages().get(0));
        verify(regionRepository, times(1)).findBySlug(testSlug);
    }

}