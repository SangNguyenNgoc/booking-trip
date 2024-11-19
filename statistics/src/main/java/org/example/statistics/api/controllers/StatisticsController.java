package org.example.statistics.api.controllers;

import lombok.RequiredArgsConstructor;
import org.example.statistics.api.dtos.GeneralStatistics;
import org.example.statistics.api.dtos.StatisticsRequest;
import org.example.statistics.api.services.interfaces.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("")
    public ResponseEntity<GeneralStatistics> getGeneralStatistics(@RequestBody StatisticsRequest statisticsRequest){
        return ResponseEntity.ok(statisticsService.getGeneralStatistics(statisticsRequest));
    }
}
