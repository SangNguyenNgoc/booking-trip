package com.example.location.api.services;

import com.example.location.api.dtos.region.RegionInfo;
import com.example.location.api.repositories.RegionRepository;
import com.example.location.api.services.interfaces.RegionService;
import com.example.location.api.services.mappers.RegionMapper;
import com.example.location.utils.dtos.ListResponse;
import com.example.location.utils.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultRegionService implements RegionService {

    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper;

    @Override
    public ListResponse<RegionInfo> getAllRegions() {
        var regions = regionRepository.findAll();
        return ListResponse.<RegionInfo>builder()
                .size(regions.size())
                .data(regions.stream().map(regionMapper::toDto).toList())
                .build();
    }

    @Override
    public RegionInfo getRegionBySlug(String slug) {
        var region = regionRepository.findBySlug(slug).orElseThrow(
                () -> new DataNotFoundException(List.of("Region not found"))
        );
        return regionMapper.toDto(region);
    }
}
