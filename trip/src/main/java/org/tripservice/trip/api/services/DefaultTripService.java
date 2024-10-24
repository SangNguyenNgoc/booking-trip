package org.tripservice.trip.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.tripservice.trip.api.documents.Schedule;
import org.tripservice.trip.api.documents.Trip;
import org.tripservice.trip.api.documents.VehicleType;
import org.tripservice.trip.api.dtos.schedule.ScheduleResponse;
import org.tripservice.trip.api.dtos.trip.TripCreate;
import org.tripservice.trip.api.dtos.trip.TripDetail;
import org.tripservice.trip.api.dtos.trip.TripInfo;
import org.tripservice.trip.api.repositories.ScheduleRepository;
import org.tripservice.trip.api.repositories.TripRepository;
import org.tripservice.trip.api.services.interfaces.TripService;
import org.tripservice.trip.api.services.mappers.ScheduleMapper;
import org.tripservice.trip.api.services.mappers.TripMapper;
import org.tripservice.trip.utils.dtos.ListResponse;
import org.tripservice.trip.utils.exception.DataNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DefaultTripService implements TripService {

    TripRepository tripRepository;
    ScheduleRepository scheduleRepository;

    TripMapper tripMapper;
    ScheduleMapper scheduleMapper;

    @Override
    public List<TripInfo> createTrip(TripCreate tripCreate) {
        var schedule = scheduleRepository.findById(tripCreate.getScheduleId()).orElseThrow(
                () -> new DataNotFoundException(List.of("Schedule not found"))
        );
        LocalDateTime start = LocalDateTime.of(tripCreate.getStartDate(), LocalTime.of(6, 0));
        LocalDateTime end = LocalDateTime.of(tripCreate.getEndDate(), LocalTime.of(23, 0));
        List<Trip> tripList = scheduleTrips(
                schedule, tripCreate.getVehicles(),
                start, end
        );
        tripRepository.saveAll(tripList);
        return tripList.stream().map(tripMapper::toInfo).collect(Collectors.toList());
    }


    @Override
    public List<Trip> scheduleTrips(Schedule schedule, List<String> vehicles, LocalDateTime start, LocalDateTime end) {
        List<Trip> trips = new ArrayList<>();
        // Vòng lặp qua từng xe
        for (var vehicle : vehicles) {
            LocalDateTime currentTime = start;
            while (!currentTime.isAfter(end)) {
                // Thời gian đến điểm B
                LocalDateTime arrivalTimeAtB = currentTime.plusMinutes(makeDurationTrip(schedule.getDuration()));

                // Kiểm tra nếu xe tới điểm đến trong khoảng thời gian delay từ 23h đến 5h
                if (isInDelayPeriod(currentTime)) {
                    // Chuyển thời gian đến 5h sáng hôm sau
                    currentTime = currentTime.getHour() <= 6
                            ? LocalDateTime.of(arrivalTimeAtB.toLocalDate(), LocalTime.of(6, 0))
                            : LocalDateTime.of(arrivalTimeAtB.toLocalDate().plusDays(1), LocalTime.of(5, 0));
                    continue;  // Bắt đầu chuyến tiếp theo sau khi delay
                }

                // Thêm chuyến đi A -> B vào danh sách
                var delayMinus = plusMinus(trips, currentTime);
                trips.add(Trip.builder()
                        .licensePlate(vehicle)
                        .scheduleId(schedule.getId())
                        .seatsAvailable(schedule.getVehicleType().getSeats().size())
                        .seatsReserved(new ArrayList<>())
                        .startTime(currentTime.plusMinutes(delayMinus))
                        .endTime(arrivalTimeAtB.plusMinutes(delayMinus))
                        .build());

                // Thời gian quay lại từ B về A
                LocalDateTime departureTimeFromB = arrivalTimeAtB;
                LocalDateTime arrivalTimeAtA = departureTimeFromB.plusMinutes(makeDurationTrip(schedule.getDuration()));

                // Kiểm tra nếu xe quay về trong khoảng thời gian delay từ 23h đến 5h
                if (isInDelayPeriod(departureTimeFromB)) {
                    // Delay đến 5h sáng hôm sau
                    currentTime = departureTimeFromB.getHour() <= 6
                            ? LocalDateTime.of(arrivalTimeAtA.toLocalDate(), LocalTime.of(5, 0))
                            : LocalDateTime.of(arrivalTimeAtA.toLocalDate().plusDays(1), LocalTime.of(5, 0));
                    continue;  // Bắt đầu chuyến tiếp theo sau khi delay
                }

                // Thêm chuyến đi B -> A vào danh sách
                var delayMinusB = plusMinus(trips, departureTimeFromB);
                trips.add(Trip.builder()
                        .licensePlate(vehicle)
                        .scheduleId(schedule.getId())
                        .seatsAvailable(schedule.getVehicleType().getSeats().size())
                        .seatsReserved(new ArrayList<>())
                        .startTime(departureTimeFromB.plusMinutes(delayMinusB))
                        .endTime(arrivalTimeAtA.plusMinutes(delayMinusB))
                        .build());

                // Cập nhật thời gian cho chuyến tiếp theo
                currentTime = arrivalTimeAtA;
            }
        }
        return trips;
    }

    @Override
    public ListResponse<ScheduleResponse> getSchedulesIncludeTripsByFromAndTo(String from, String to, LocalDate date) {
        var schedules = scheduleRepository.findByRegionFromAndRegionTo(from, to);
        Sort sort = Sort.by(Sort.Order.asc("startTime"));

        var trips = tripRepository.findTripsBySchedulesAndStartTime(
                schedules.stream().map(Schedule::getId).collect(Collectors.toList()),
                date,
                date.plusDays(1),
                sort
        );
        var scheduleResponses = schedules.stream()
                .map(scheduleMapper::toResponse)
                .collect(Collectors.toList());
        scheduleResponses.forEach(scheduleResponse -> {
            List<TripInfo> tripInfoList = trips.stream()
                    .filter(trip -> trip.getScheduleId().equals(scheduleResponse.getId()))
                    .map(tripMapper::toInfo)
                    .collect(Collectors.toList());
            scheduleResponse.setTrips(tripInfoList);
        });
        return ListResponse.<ScheduleResponse>builder()
                .size(scheduleResponses.size())
                .data(scheduleResponses)
                .build();
    }


    @Override
    public TripDetail getTripDetail(String tripId) {
        var trip = tripRepository.findById(tripId).orElseThrow(
                () -> new DataNotFoundException(List.of("Trip not found"))
        );
        var schedule = scheduleRepository.findById(trip.getScheduleId()).orElseThrow(
                () -> new DataNotFoundException(List.of("Schedule not found"))
        );
        var tripDetail = tripMapper.toDetail(trip);
        var scheduleDetail = scheduleMapper.toDetail(schedule);
        tripDetail.setSchedule(scheduleDetail);
        tripDetail.setSeats(mapSeat(schedule.getVehicleType(), trip.getSeatsReserved()));
        return tripDetail;
    }

    @Override
    public TripDetail getTripDetailForBooking(String tripId) {
        var trip = tripRepository.findById(tripId).orElseThrow(
                () -> new DataNotFoundException(List.of("Trip not found"))
        );
        var schedule = scheduleRepository.findById(trip.getScheduleId()).orElseThrow(
                () -> new DataNotFoundException(List.of("Schedule not found"))
        );
        var tripDetail = tripMapper.toDetail(trip);
        var scheduleDetail = scheduleMapper.toDetail(schedule);
        tripDetail.setSchedule(scheduleDetail);
        return tripDetail;
    }

    public List<TripDetail.SeatDto> mapSeat(VehicleType vehicleType, List<String> seatsReserved) {
        List<TripDetail.SeatDto> seats = vehicleType.getSeats().stream().map(seat -> {
            var seatDto = TripDetail.SeatDto.builder()
                    .id(seat.getId())
                    .rowNo(seat.getRowNo())
                    .colNo(seat.getColNo())
                    .floorNo(seat.getFloorNo())
                    .name(seat.getName())
                    .build();
            seatDto.setIsReserved(seatsReserved.contains(seat.getName()));
            return seatDto;
        }).collect(Collectors.toList());
        return seats;
    }

    public Integer makeDurationTrip(Double durationSchedule) {
        durationSchedule = durationSchedule * 1.5;
        int minus = (int) durationSchedule.doubleValue();
        return (((minus + 59) / 60) * 60);
    }


    public boolean isInDelayPeriod(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();
        return (time.isAfter(LocalTime.of(23, 0)) || time.isBefore(LocalTime.of(6, 0)));
    }


    public int plusMinus(List<Trip> trips, LocalDateTime dateTime) {
        int result = 0;
        while (true) {
            LocalDateTime temp = dateTime;
            Trip trip = trips.stream().filter(t -> t.getStartTime().equals(temp)).findFirst().orElse(null);
            if (trip == null) {
                break;
            } else {
                result += 30;
                dateTime = dateTime.plusMinutes(30);
            }
        }
        return result;
    }
}
