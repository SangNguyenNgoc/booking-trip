package org.example.statistics.api.services;

import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.example.statistics.api.documents.Bill;
import org.example.statistics.api.documents.Schedule;
import org.example.statistics.api.documents.Trip;
import org.example.statistics.api.documents.Type;
import org.example.statistics.api.dtos.*;
import org.example.statistics.api.repositories.BillRepository;
import org.example.statistics.api.repositories.ScheduleRepository;
import org.example.statistics.api.repositories.TripRepository;
import org.example.statistics.api.services.interfaces.StatisticsService;
import org.example.statistics.utils.exception.DataNotFoundException;
import org.example.statistics.utils.exception.InputInvalidException;
import org.springframework.data.util.TypeCollector;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.example.statistics.api.documents.Type.MONTH;

@Service
@RequiredArgsConstructor
public class DefaultStatisticsService implements StatisticsService {
    private final BillRepository billRepository;
    private final ScheduleRepository scheduleRepository;
    private final TripRepository tripRepository;

    @KafkaListener(topics = "BillCreated")
    private void createBill(List<Bill> bills) {
        billRepository.saveAll(bills);
    }

    @KafkaListener(topics = "ScheduleCreated")
    private void createSchedule(Schedule schedule) {
        var oldSchedule = scheduleRepository.findById(schedule.getId());
        if (oldSchedule.isEmpty()) {
            scheduleRepository.save(schedule);
        }
    }

    @KafkaListener(topics = "StatisticTrips")
    private void createTrip(List<Trip> trips) {
        tripRepository.saveAll(trips);
    }

    public List<BillChart> createBillChart(Integer time, Type type) {
        List<BillChart> billCharts = new ArrayList<>();
//      //Ngày đầu trong năm
        LocalDateTime startOfYear = Year.of(time).atDay(1).atStartOfDay();
        // Ngày cuối cùng của năm
        LocalDateTime endOfYear = Year.of(time).atDay(Year.of(time).length()).atTime(23, 59, 59);
        var bills = billRepository.findByCreateDateBetween(startOfYear, endOfYear);
        switch (type) {
            case MONTH -> {
                var lastMonth = 0D;
                for (int i = 1; i <= 12; i++) {
                    int finalI = i;
                    var bill = bills.stream()
                            .filter(b -> b.getCreateDate().getMonthValue() == finalI)
                            .toList();
                    var sum = bill.stream().mapToDouble(Bill::getTotalPrice).sum();
                    var ratio = 0D;
                    if (lastMonth == 0) {
                        ratio = sum == 0 ? 0 : 100;
                    } else {
                        ratio = (sum / lastMonth) * 100;
                    }
                    billCharts.add(BillChart.builder()
                            .numberOfBill(bill.size())
                            .ratio(ratio)
                            .type(MONTH.getValue())
                            .time(i)
                            .revenue(sum)
                            .build());
                    lastMonth = sum;
                }

            }
            case DAY -> {
                var lastDay = 0D;
                for (int day = 1; day <= 365; day++) {  // Iterating over days in a year
                    var currentDate = LocalDate.ofYearDay(time, day);  // Get the date for the current day

                    // Filter bills by current date
                    var bill = bills.stream()
                            .filter(b -> b.getCreateDate().toLocalDate().equals(currentDate))  // Convert to LocalDate and compare
                            .toList();

                    // Calculate the sum for the current day
                    var sum = bill.stream().mapToDouble(Bill::getTotalPrice).sum();

                    var ratio = 0D;
                    if (lastDay == 0) {
                        ratio = sum == 0 ? 0 : 100;  // If it's the first day, set the ratio to 100 if there's any sum
                    } else {
                        ratio = (sum / lastDay) * 100;  // Calculate ratio relative to the previous day
                    }

                    // Add to your chart list
                    billCharts.add(BillChart.builder()
                            .numberOfBill(bill.size())  // Total number of bills for the day
                            .ratio(ratio)               // Percentage change from the previous day
                            .type(Type.DAY.getValue())
                            .time(day)
                            .revenue(sum)
                            .build());

                    lastDay = sum;
                }
            }
            case QUARTER -> {
                var lastQuarterSum = 0D;
                for (int quarter = 1; quarter <= 4; quarter++) {  // Iterate over 4 quarters
                    // Determine the months for the current quarter
                    int startMonth = (quarter - 1) * 3 + 1;  // Start month for the quarter (1, 4, 7, 10)
                    int endMonth = startMonth + 2;           // End month for the quarter (3, 6, 9, 12)

                    // Filter bills by month range for the current quarter
                    var billsInQuarter = bills.stream()
                            .filter(b -> {
                                int month = b.getCreateDate().getMonthValue();
                                return month >= startMonth && month <= endMonth;  // Check if the bill is in the current quarter's months
                            })
                            .toList();

                    // Calculate the sum for the current quarter
                    var sum = billsInQuarter.stream().mapToDouble(Bill::getTotalPrice).sum();

                    var ratio = 0D;
                    if (lastQuarterSum == 0) {
                        ratio = sum == 0 ? 0 : 100;  // If it's the first quarter, set the ratio to 100 if there's any sum
                    } else {
                        ratio = (sum / lastQuarterSum) * 100;  // Calculate ratio relative to the previous quarter
                    }

                    // Add to your chart list for the current quarter
                    billCharts.add(BillChart.builder()
                            .numberOfBill(billsInQuarter.size())  // Total number of bills for the quarter
                            .ratio(ratio)   // Percentage change from the previous quarter
                            .type(Type.QUARTER.getValue())
                            .time(quarter)
                            .revenue(sum)
                            .build());

                    lastQuarterSum = sum;  // Update lastQuarterSum for the next iteration
                }
            }
            default -> throw new BadRequestException("Server error");
        }
        return billCharts;
    }

    private List<TripChart> createTripChart(Integer time, Type type) {
        List<TripChart> tripCharts = new ArrayList<>();

        // Start and end of the year
        LocalDateTime startOfYear = Year.of(time).atDay(1).atStartOfDay();
        LocalDateTime endOfYear = Year.of(time).atDay(Year.of(time).length()).atTime(23, 59, 59);
        var trips = tripRepository.findByStartTimeBetween(startOfYear, endOfYear);  // Fetch trips for the year

        switch (type) {
            case MONTH -> {
                var lastMonth = 0D;
                for (int i = 1; i <= 12; i++) {
                    int finalI = i;
                    var trip = trips.stream()
                            .filter(t -> t.getStartTime().getMonthValue() == finalI)
                            .toList();

                    var sumSeats = trip.stream().mapToDouble(Trip::getTotalSeats).sum();
                    var sumSeatsReserved = trip.stream().mapToDouble(Trip::getSeatsReserved).sum();
                    var occupancyRate = (sumSeatsReserved / sumSeats) * 100;
                    var ratio = 0D;
                    if (lastMonth == 0) {
                        ratio = occupancyRate == 0 ? 0 : 100;
                    } else {
                        ratio = (occupancyRate / lastMonth) * 100;
                    }
                    tripCharts.add(TripChart.builder()
                            .numberOfTrip(trip.size())
                            .ratio(ratio)
                            .occupancyRate(occupancyRate)
                            .type(MONTH.getValue())
                            .time(i)
                            .build());
                    lastMonth = occupancyRate;
                }
            }
            case DAY -> {
                var lastDay = 0D;
                boolean isLeapYear = LocalDate.of(time, 1, 1).isLeapYear();
                var allDay = isLeapYear ? 366 : 365;
                for (int day = 1; day <= allDay; day++) {  // Iterating over days in a year
                    var currentDate = LocalDate.ofYearDay(time, day);  // Get the date for the current day

                    // Filter trips by current date
                    var trip = trips.stream()
                            .filter(t -> t.getStartTime().toLocalDate().equals(currentDate))
                            .toList();

                    // Calculate the sum for the current day
                    var sumSeats = trip.stream().mapToDouble(Trip::getTotalSeats).sum();
                    var sumSeatsReserved = trip.stream().mapToDouble(Trip::getSeatsReserved).sum();
                    var occupancyRate = (sumSeatsReserved / sumSeats) * 100;
                    var ratio = 0D;
                    if (lastDay == 0) {
                        ratio = occupancyRate == 0 ? 0 : 100;
                    } else {
                        ratio = (occupancyRate / lastDay) * 100;
                    }

                    // Add to your chart list
                    tripCharts.add(TripChart.builder()
                            .numberOfTrip(trip.size())  // Total number of trips for the day
                            .ratio(ratio)
                            .occupancyRate(occupancyRate)// Percentage change from the previous day
                            .type(Type.DAY.getValue())
                            .time(day)
                            .build());

                    lastDay = occupancyRate;
                }
            }
            case QUARTER -> {
                var lastQuarterSum = 0D;
                for (int quarter = 1; quarter <= 4; quarter++) {  // Iterate over 4 quarters
                    int startMonth = (quarter - 1) * 3 + 1;
                    int endMonth = startMonth + 2;

                    // Filter trips by month range for the current quarter
                    var tripsInQuarter = trips.stream()
                            .filter(t -> {
                                int month = t.getStartTime().getMonthValue();
                                return month >= startMonth && month <= endMonth;
                            })
                            .toList();

                    // Calculate the sum for the current quarter
                    var sumSeats = tripsInQuarter.stream().mapToDouble(Trip::getTotalSeats).sum();
                    var sumSeatsReserved = tripsInQuarter.stream().mapToDouble(Trip::getSeatsReserved).sum();
                    var occupancyRate = (sumSeatsReserved / sumSeats) * 100;
                    var ratio = 0D;
                    if (lastQuarterSum == 0) {
                        ratio = occupancyRate == 0 ? 0 : 100;
                    } else {
                        ratio = (occupancyRate / lastQuarterSum) * 100;
                    }

                    // Add to your chart list for the current quarter
                    tripCharts.add(TripChart.builder()
                            .numberOfTrip(tripsInQuarter.size())  // Total number of trips for the quarter
                            .ratio(ratio)
                            .occupancyRate(occupancyRate)// Percentage change from the previous quarter
                            .type(Type.QUARTER.getValue())
                            .time(quarter)
                            .build());

                    lastQuarterSum = occupancyRate;
                }
            }
            default -> throw new BadRequestException("Server error");
        }

        return tripCharts;
    }

    public GeneralStatistics getGeneralStatistics(StatisticsRequest statisticsRequest) {
        var year = statisticsRequest.getYear();
        var typeChart = statisticsRequest.getTypeChart();
        var type = statisticsRequest.getType();
        var time = statisticsRequest.getTime();
        if (year < 1900 || year >= 3000)
            throw new InputInvalidException(List.of("The year ranges from 1901 to 2099"));
        GeneralStatistics generalStatistics = null;
        switch (type) {
            case YEAR -> {
                if (!time.equals(year)) {
                    System.out.println(time+"    "+year);
                    throw new InputInvalidException(List.of("Invalid time"));
                }
                var billCharts = createBillChart(year, typeChart);
                var billChartsLastYear = createBillChart(year - 1, typeChart);
                var tripCharts = createTripChart(year, typeChart);
                var revenue = billCharts.stream()
                        .mapToDouble(BillChart::getRevenue)
                        .sum();
                var revenueLastYear = billChartsLastYear.stream()
                        .mapToDouble(BillChart::getRevenue)
                        .sum();
                var ratio = 0D;
                if (revenueLastYear == 0) {
                    ratio = revenue == 0 ? 0 : 100;
                } else {
                    ratio = revenue == 0 ? -100 : (revenue / revenueLastYear) * 100;
                }
                generalStatistics = GeneralStatistics.builder()
                        .revenue(revenue)
                        .time(time)
                        .type(Type.YEAR.getValue())
                        .ratio(ratio)
                        .billChart(billCharts)
                        .tripChart(tripCharts)
                        .build();
            }
            case QUARTER -> {
                if (time > 4 || time < 1) throw new InputInvalidException(List.of("Invalid time"));
                if (typeChart.equals(Type.QUARTER)) throw new InputInvalidException(List.of("Invalid type chart"));
                var billCharts = createBillChart(year, typeChart);
                var tripCharts = createTripChart(year, typeChart);
                switch (typeChart) {
                    case MONTH -> {
                        var billChartsLastQuarter = billCharts.stream()
                                .filter(b -> b.getTime() <= ((time - 1) * 3 + 3) && b.getTime() > ((time - 1) * 3))
                                .toList();
                        var billChartsQuarter = billCharts.stream()
                                .filter(b -> b.getTime() <= (time * 3 + 3) && b.getTime() > (time * 3))
                                .toList();

                        var tripChartQuater = tripCharts.stream()
                                .filter(b -> b.getTime() <= (time * 3 + 3) && b.getTime() > (time * 3))
                                .toList();

                        var revenue = billChartsQuarter.stream()
                                .mapToDouble(BillChart::getRevenue)
                                .sum();
                        var revenueLastQuarter = billChartsLastQuarter.stream()
                                .mapToDouble(BillChart::getRevenue)
                                .sum();
                        var ratio = 0D;
                        if (revenueLastQuarter == 0) {
                            ratio = revenue == 0 ? 0 : 100;
                        } else {
                            ratio = revenue == 0 ? -100 : (revenue / revenueLastQuarter) * 100;
                        }
                        generalStatistics = GeneralStatistics.builder()
                                .revenue(revenue)
                                .time(time)
                                .type(Type.YEAR.getValue())
                                .ratio(ratio)
                                .billChart(billChartsQuarter)
                                .tripChart(tripChartQuater)
                                .build();
                    }
                    case DAY -> {
                        var billChartsLastQuarter =
                                extractDaysForMonth(billCharts, year, ((time - 1)*3) + 1, (time-1) * 3 + 3);
                        var billChartsQuarter =
                                extractDaysForMonth(billCharts, year, (time*3) + 1, time * 3 + 3);
                        var tripChartsQuater =
                                extractDaysForMonth(tripCharts, year, (time*3) + 1, time * 3 + 3);
                        var revenue = billChartsQuarter.stream()
                                .mapToDouble(BillChart::getRevenue)
                                .sum();
                        var revenueLastQuarter = billChartsLastQuarter.stream()
                                .mapToDouble(BillChart::getRevenue)
                                .sum();
                        var ratio = 0D;
                        if (revenueLastQuarter == 0) {
                            ratio = revenue == 0 ? 0 : 100;
                        } else {
                            ratio = revenue == 0 ? -100 : (revenue / revenueLastQuarter) * 100;
                        }
                        generalStatistics = GeneralStatistics.builder()
                                .revenue(revenue)
                                .time(time)
                                .type(Type.QUARTER.getValue())
                                .ratio(ratio)
                                .billChart(billChartsQuarter)
                                .tripChart(tripChartsQuater)
                                .build();
                    }
                }
            }
            case MONTH -> {
                if (time > 12 || time < 1) throw new InputInvalidException(List.of("Invalid time"));
                if (typeChart.equals(Type.QUARTER) || typeChart.equals(MONTH))
                    throw new InputInvalidException(List.of("Invalid type chart"));
                var billCharts = createBillChart(year, typeChart);
                var tripCharts = createTripChart(year, typeChart);
                var billChartsLastMonth =
                        extractDaysForMonth(billCharts, year, time, time);
                var billChartsMonth =
                        extractDaysForMonth(billCharts, year, time, time);

                var tripChartMonth =
                        extractDaysForMonth(tripCharts, year, time, time);

                var revenue = billChartsMonth.stream()
                        .mapToDouble(BillChart::getRevenue)
                        .sum();
                var revenueLastQuarter = billChartsLastMonth.stream()
                        .mapToDouble(BillChart::getRevenue)
                        .sum();

                var ratio = 0D;
                if (revenueLastQuarter == 0) {
                    ratio = revenue == 0 ? 0 : 100;
                } else {
                    ratio = revenue == 0 ? -100 : (revenue / revenueLastQuarter) * 100;
                }
                IntStream.range(0, billChartsMonth.size())
                        .forEach(index -> billChartsMonth.get(index).setTime(index + 1));
                IntStream.range(0, tripChartMonth.size())
                        .forEach(index -> tripChartMonth.get(index).setTime(index + 1));
                generalStatistics = GeneralStatistics.builder()
                        .revenue(revenue)
                        .time(time)
                        .type(MONTH.getValue())
                        .ratio(ratio)
                        .billChart(billChartsMonth)
                        .tripChart(tripChartMonth)
                        .build();
            }
        }
        return generalStatistics;
    }

    private <T> List<T> extractDaysForMonth(List<T> list, Integer year, Integer startMonth, Integer endMonth) {
        if (startMonth < 1 || endMonth > 12 || startMonth > endMonth) {
            System.out.println(startMonth+"   "+endMonth);
            throw new InputInvalidException(List.of("Tháng phải nằm trong khoảng từ 1 đến 12."));
        }

        // Xác định ngày đầu tiên và ngày cuối cùng của tháng
        LocalDate startOfMonth = LocalDate.of(year, startMonth, 1);
        YearMonth yearMonth = YearMonth.of(year, endMonth);
        LocalDate endOfMonth = yearMonth.atEndOfMonth(); // Ngày cuối cùng của tháng


        // Tính vị trí bắt đầu và kết thúc trong mảng
        int startIndex = startOfMonth.getDayOfYear() - 1; // Bắt đầu từ 0
        int endIndex = endOfMonth.getDayOfYear();         // Kết thúc tại vị trí (endIndex - 1)
        System.out.println(endIndex);

        // Trích xuất phần tử từ mảng

        return  list.subList(startIndex, endIndex);
    }


    public TripStatistics getTripStatistics(String id){
        var trip = tripRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(List.of("Not found trip with id " + id)));
        var bills = billRepository.findBillByTripId(id);

        return TripStatistics.builder()
                .numberOfTicket(trip.getSeatsReserved())
                .startTime(trip.getStartTime())
                .revenue(bills.stream().mapToDouble(Bill::getTotalPrice).sum())
                .occupancyRate((double) (trip.getSeatsReserved()/ trip.getTotalSeats()))
                .build();
    }

    public ScheduleStatistics getScheduleStatistics(String id, LocalDate from, LocalDate to){
        var schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(List.of("Not found schedule with id " + id)));
        var trips = tripRepository.findByScheduleIdAndStartTimeBetween(id, from.atStartOfDay(), to.atStartOfDay());
        var bills = billRepository.findAll();
        var occupancyRate = trips.stream().mapToDouble(Trip::getSeatsReserved).sum()/trips.stream().mapToDouble(Trip::getTotalSeats).sum();

        return ScheduleStatistics.builder()
                .numberOfTicket(trips.stream().mapToInt(Trip::getSeatsReserved).sum())
                .occupancyRate(occupancyRate)
                .revenue(bills.stream()
                        .filter(b -> trips.stream().anyMatch(t -> t.getId().equals(b.getTripId())))
                        .mapToDouble(Bill::getTotalPrice)
                        .sum())
                .trip(trips.stream().map(t -> TripStatistics.builder()
                        .numberOfTicket(t.getSeatsReserved())
                        .startTime(t.getStartTime())
                        .revenue(bills.stream()
                                .filter(b -> b.getTripId().equals(t.getId()))
                                .mapToDouble(Bill::getTotalPrice).sum())
                        .occupancyRate((double) (t.getSeatsReserved()/ t.getTotalSeats()))
                        .build())
                        .toList())
                .build();
    }

}
