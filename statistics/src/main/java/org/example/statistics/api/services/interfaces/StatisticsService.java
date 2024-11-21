package org.example.statistics.api.services.interfaces;

import org.example.statistics.api.documents.Type;
import org.example.statistics.api.dtos.GeneralStatistics;
import org.example.statistics.api.dtos.ScheduleStatistics;
import org.example.statistics.api.dtos.StatisticsRequest;
import org.example.statistics.api.dtos.TripStatistics;

import java.time.LocalDate;

public interface StatisticsService {
    GeneralStatistics getGeneralStatistics(StatisticsRequest statisticsRequest);
    TripStatistics getTripStatistics(String id);
    ScheduleStatistics getScheduleStatistics(String id, LocalDate from, LocalDate to);
}
