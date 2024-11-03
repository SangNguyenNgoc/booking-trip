package org.tripservice.trip.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tripservice.trip.api.documents.Schedule;
import org.tripservice.trip.api.documents.Trip;
import org.tripservice.trip.api.documents.VehicleType;
import org.tripservice.trip.api.dtos.booking.BookingEvent;
import org.tripservice.trip.api.dtos.schedule.AssignSchedule;
import org.tripservice.trip.api.dtos.schedule.ScheduleResponse;
import org.tripservice.trip.api.dtos.trip.TripCreate;
import org.tripservice.trip.api.dtos.trip.TripDetail;
import org.tripservice.trip.api.dtos.trip.TripInfo;
import org.tripservice.trip.api.repositories.ScheduleRepository;
import org.tripservice.trip.api.repositories.TripRepository;
import org.tripservice.trip.api.repositories.VehicleTypeRepository;
import org.tripservice.trip.api.services.interfaces.TripService;
import org.tripservice.trip.api.services.mappers.ScheduleMapper;
import org.tripservice.trip.api.services.mappers.TripMapper;
import org.tripservice.trip.utils.dtos.ListResponse;
import org.tripservice.trip.utils.exception.DataNotFoundException;
import org.tripservice.trip.utils.exception.InputInvalidException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DefaultTripService implements TripService {

    TripRepository tripRepository;
    ScheduleRepository scheduleRepository;
    VehicleTypeRepository vehicleTypeRepository;

    TripMapper tripMapper;
    ScheduleMapper scheduleMapper;

    KafkaTemplate<String, Object> kafkaTemplate;
    MongoTemplate mongoTemplate;

    public static Map<String, List<String>> groupByTripId(List<BookingEvent> events) {
        return events.stream()
                .collect(Collectors.groupingBy(
                        BookingEvent::getTripId,
                        Collectors.flatMapping(event -> event.getSeats().stream(), Collectors.toList())
                ));
    }

    @Override
    public List<TripInfo> createTrip(TripCreate tripCreate) {
        validateVehicleAvailability(tripCreate);

        Schedule schedule = getScheduleById(tripCreate.getScheduleId());
        Schedule contrarySchedule = getScheduleByIdAndVehicleType(tripCreate.getContraryScheduleId(), schedule.getVehicleTypeId());
        VehicleType vehicleType = getVehicleTypeById(schedule.getVehicleTypeId());

        LocalDateTime start = LocalDateTime.of(tripCreate.getStartDate(), LocalTime.of(6, 0));
        LocalDateTime end = LocalDateTime.of(tripCreate.getEndDate(), LocalTime.of(23, 0));

        List<Trip> availableTrips = tripRepository.findAllByStartTimeBeforeAndStartTimeAfter(tripCreate.getStartDate(), tripCreate.getEndDate());

        List<Trip> tripList = scheduleTrips(schedule, contrarySchedule, vehicleType, availableTrips,
                tripCreate.getVehicles(), start, end);

        tripRepository.saveAll(tripList);
        notifyVehicleAssignment(tripCreate, start, end, schedule);

        return tripList.stream()
                .map(tripMapper::toInfo)
                .collect(Collectors.toList());
    }

    private void validateVehicleAvailability(TripCreate tripCreate) {
        var licensePlateList = tripRepository.checkVehicleInTime(
                tripCreate.getVehicles(), tripCreate.getStartDate(), tripCreate.getEndDate()
        );
        if (!licensePlateList.isEmpty()) {
            throw new InputInvalidException(List.of("Vehicle is unavailable in this time"));
        }
    }

    private Trip getTripById(String tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new DataNotFoundException(List.of("Trip not found")));
    }

    private Schedule getScheduleById(String scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new DataNotFoundException(List.of("Schedule not found")));
    }

    private Schedule getScheduleByIdAndVehicleType(String scheduleId, Long vehicleTypeId) {
        return scheduleRepository.findByIdAndVehicleTypeId(scheduleId, vehicleTypeId)
                .orElseThrow(() -> new DataNotFoundException(List.of("Schedule not found with type id")));
    }

    private VehicleType getVehicleTypeById(Long vehicleTypeId) {
        return vehicleTypeRepository.findById(vehicleTypeId)
                .orElseThrow(() -> new DataNotFoundException(List.of("Vehicle type not found")));
    }

    private void notifyVehicleAssignment(TripCreate tripCreate, LocalDateTime start, LocalDateTime end, Schedule schedule) {
        String route = schedule.getRegionFrom().getName() + " - " + schedule.getRegionTo().getName();
        kafkaTemplate.send("vehiclesAreAssigned", createAssignSchedule(
                tripCreate.getVehicles(), start.toLocalDate(), end.toLocalDate(), route
        ));
    }

    @Override
    public List<Trip> scheduleTrips(
            Schedule schedule, Schedule contrarySchedule, VehicleType vehicleType,
            List<Trip> availableTrip, List<String> vehicles, LocalDateTime start, LocalDateTime end
    ) {

        var seats = vehicleType.getSeats();
        var seatsWithNames = seats.stream().filter(seat -> seat.getName() != null).toList();
        var firstFloorSeats = seats.stream().filter(seat -> seat.getFloorNo() == 1 && seat.getName() != null).toList();
        var secondFloorSeats = seats.stream().filter(seat -> seat.getFloorNo() == 2 && seat.getName() != null).toList();

        List<Trip> trips = new ArrayList<>();
        // Vòng lặp qua từng xe
        for (var vehicle : vehicles) {
            LocalDateTime currentTime = start;
            while (!currentTime.isAfter(end)) {
                // Thời gian đến điểm B
                LocalDateTime arrivalTimeAtB = currentTime.plusMinutes(makeDurationTrip(schedule.getDuration()));

                if (arrivalTimeAtB.isAfter(end) || arrivalTimeAtB.plusMinutes(makeDurationTrip(contrarySchedule.getDuration())).isAfter(end)) {
                    break;
                }

                var delayMinus = plusMinus(availableTrip, currentTime, schedule.getId());
                currentTime = currentTime.plusMinutes(delayMinus);
                // Kiểm tra nếu xe tới điểm đến trong khoảng thời gian delay từ 23h đến 5h
                if (isInDelayPeriod(currentTime)) {
                    // Chuyển thời gian đến 5h sáng hôm sau
                    currentTime = currentTime.getHour() <= 6
                            ? LocalDateTime.of(arrivalTimeAtB.toLocalDate(), LocalTime.of(6, 0))
                            : LocalDateTime.of(arrivalTimeAtB.toLocalDate().plusDays(1), LocalTime.of(6, 0));
                    continue;  // Bắt đầu chuyến tiếp theo sau khi delay
                }

                // Thêm chuyến đi A -> B vào danh sách
                var trip = Trip.builder()
                        .licensePlate(vehicle)
                        .scheduleId(schedule.getId())
                        .seatsAvailable(seatsWithNames.size())
                        .price(schedule.getPrice())
                        .totalSeats(seatsWithNames.size())
                        .firstFloorSeats(firstFloorSeats.size())
                        .secondFloorSeats(secondFloorSeats.size())
                        .seatsReserved(new ArrayList<>())
                        .startTime(currentTime)
                        .endTime(arrivalTimeAtB.plusMinutes(delayMinus))
                        .build();
                trips.add(trip);
                availableTrip.add(trip);

                // Thời gian quay lại từ B về A
                LocalDateTime departureTimeFromB = arrivalTimeAtB;

                var delayMinusB = plusMinus(availableTrip, departureTimeFromB, contrarySchedule.getId());
                departureTimeFromB = departureTimeFromB.plusMinutes(delayMinusB);
                // Kiểm tra nếu xe quay về trong khoảng thời gian delay từ 23h đến 5h
                if (isInDelayPeriod(departureTimeFromB)) {
                    // Delay đến 5h sáng hôm sau
                    departureTimeFromB = departureTimeFromB.getHour() <= 6
                            ? LocalDateTime.of(departureTimeFromB.toLocalDate(), LocalTime.of(6, 0))
                            : LocalDateTime.of(departureTimeFromB.toLocalDate().plusDays(1), LocalTime.of(6, 0));
                }
                LocalDateTime arrivalTimeAtA = departureTimeFromB.plusMinutes(makeDurationTrip(contrarySchedule.getDuration()));

                // Thêm chuyến đi B -> A vào danh sách
                var contraryTRip = Trip.builder()
                        .licensePlate(vehicle)
                        .scheduleId(contrarySchedule.getId())
                        .seatsAvailable(seatsWithNames.size())
                        .price(schedule.getPrice())
                        .totalSeats(seatsWithNames.size())
                        .firstFloorSeats(firstFloorSeats.size())
                        .secondFloorSeats(secondFloorSeats.size())
                        .seatsReserved(new ArrayList<>())
                        .startTime(departureTimeFromB)
                        .endTime(arrivalTimeAtA.plusMinutes(delayMinusB))
                        .build();
                trips.add(contraryTRip);
                availableTrip.add(contraryTRip);

                // Cập nhật thời gian cho chuyến tiếp theo
                currentTime = arrivalTimeAtA;
            }
        }
        return trips;
    }

    @Override
    public ListResponse<ScheduleResponse> getSchedulesIncludeTripsByFromAndTo(
            String from, String to, LocalDate fromDate,
            Integer ticketCount, String timeInDay, Long vehicleType, Integer floorNo
    ) {
        List<Schedule> schedules;
        if (vehicleType != null) {
            schedules = scheduleRepository.findByRegionFromAndRegionTo(from, to, vehicleType);
        } else {
            schedules = scheduleRepository.findByRegionFromAndRegionTo(from, to);
        }
        Sort sort = Sort.by(Sort.Order.asc("startTime"));

        var trips = tripRepository.findTripsBySchedulesAndStartTime(
                schedules.stream().map(Schedule::getId).collect(Collectors.toList()),
                fromDate,
                fromDate.plusDays(1),
                sort
        );

        var scheduleResponses = schedules.stream()
                .map(scheduleMapper::toResponse)
                .collect(Collectors.toList());
        scheduleResponses.forEach(scheduleResponse -> {
            List<Trip> tripList = trips.stream()
                    .filter(trip -> trip.getScheduleId().equals(scheduleResponse.getId()))
                    .filter(trip -> ticketCount == null || trip.getSeatsAvailable() > ticketCount)
                    .collect(Collectors.toList());

            if (timeInDay != null && !timeInDay.isEmpty()) {
                tripList = filterByTimeSlots(List.of(timeInDay.split("-")), tripList);
            }

            if (floorNo != null) {
                tripList = filterByFloor(floorNo, tripList);
            }

            if (!tripList.isEmpty()) {
                Duration duration = Duration.between(tripList.get(0).getStartTime(), tripList.get(0).getEndTime());
                double minutes = duration.toMinutes();
                scheduleResponse.setDuration(minutes);
            }

            scheduleResponse.setTrips(tripList.stream().map(tripMapper::toInfo).collect(Collectors.toList()));
        });

        return ListResponse.<ScheduleResponse>builder()
                .size(scheduleResponses.size())
                .data(scheduleResponses)
                .build();
    }


    public List<Trip> filterByTimeSlots(List<String> periods, List<Trip> trips) {
        List<Trip> result = new ArrayList<>();

        for (Trip trip : trips) {
            LocalTime tripStartTime = trip.getStartTime().toLocalTime();

            boolean matchesAnyPeriod = periods.stream().anyMatch(period -> {
                LocalTime start;
                LocalTime end;

                switch (period) {
                    case "midnight" -> {
                        start = LocalTime.of(0, 0);
                        end = LocalTime.of(6, 0);
                    }
                    case "morning" -> {
                        start = LocalTime.of(6, 0);
                        end = LocalTime.of(12, 0);
                    }
                    case "afternoon" -> {
                        start = LocalTime.of(12, 0);
                        end = LocalTime.of(18, 0);
                    }
                    case "evening" -> {
                        start = LocalTime.of(18, 0);
                        end = LocalTime.of(23, 59, 59);
                    }
                    default -> throw new InputInvalidException(List.of("Time in day not valid: " + period));
                }

                return !tripStartTime.isBefore(start) && tripStartTime.isBefore(end);
            });

            if (matchesAnyPeriod) {
                result.add(trip);
            }
        }

        return result;
    }


    public List<Trip> filterByFloor( Integer floorNo, List<Trip> trips) {
        List<Trip> result = trips.stream()
                .filter(trip -> {
                    List<String> seatsReserved = trip.getSeatsReserved();
                    if (floorNo == 1) {
                        long seatInFloor = seatsReserved.stream().filter(seat -> seat.startsWith("A")).count();
                        return seatInFloor < trip.getFirstFloorSeats();
                    } else if (floorNo == 2) {
                        long seatInFloor = seatsReserved.stream().filter(seat -> seat.startsWith("B")).count();
                        return seatInFloor < trip.getSecondFloorSeats();
                    }
                    return false;
                })
                .collect(Collectors.toList());
        return result;
    }


    @Override
    public List<ScheduleResponse> getSchedulesIncludeTripsByFromAndTo(String from, String to, LocalDate date, Long vehicleTypeId) {
        List<Schedule> schedules;
        if (vehicleTypeId != null) {
            schedules = scheduleRepository.findByRegionFromAndRegionTo(from, to, vehicleTypeId);
        } else {
            schedules = scheduleRepository.findByRegionFromAndRegionTo(from, to);
        }
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
        return scheduleResponses;
    }

    @Override
    public TripDetail getTripDetail(String tripId) {
        var trip = getTripById(tripId);
        var schedule = getScheduleById(trip.getScheduleId());
        var vehicleType = getVehicleTypeById(schedule.getVehicleTypeId());
        var tripDetail = tripMapper.toDetail(trip);
        var scheduleDetail = scheduleMapper.toDetail(schedule);
        tripDetail.setSchedule(scheduleDetail);
        tripDetail.setSeats(mapSeat(vehicleType, trip.getSeatsReserved()));
        return tripDetail;
    }

    @Override
    public TripDetail getTripDetailForBooking(String tripId) {
        var trip = getTripById(tripId);
        var schedule = getScheduleById(trip.getScheduleId());
        var tripDetail = tripMapper.toDetail(trip);
        var scheduleDetail = scheduleMapper.toDetail(schedule);
        tripDetail.setSchedule(scheduleDetail);
        return tripDetail;
    }

    @Override
    @Transactional
    @KafkaListener(topics = "BillIsBooked")
    public void billIsBooked(BookingEvent bookingEvent) {
        Query tripQuery = new Query(Criteria.where("_id").is(bookingEvent.getTripId()));
        Update tripUpdate = new Update()
                .push("seatsReserved").each(bookingEvent.getSeats())
                .inc("seatsAvailable", -bookingEvent.getSeats().size());
        mongoTemplate.updateFirst(tripQuery, tripUpdate, Trip.class);

        var trip = getTripById(bookingEvent.getTripId());
        Query scheduleQuery = new Query(Criteria.where("_id").is(trip.getScheduleId()));
        Update scheduleUpdate = new Update().inc("bookedCount", bookingEvent.getSeats().size());
        mongoTemplate.updateFirst(scheduleQuery, scheduleUpdate, Schedule.class);
    }

    @Override
    @Transactional
    @KafkaListener(topics = "BillIsExpired")
    public void billIsExpired(List<BookingEvent> bookingEvents) {
        var tripIds = bookingEvents.stream()
                .map(BookingEvent::getTripId)
                .collect(Collectors.toSet());
        var trips = tripRepository.findAllById(tripIds);
        var tripMap = groupByTripId(bookingEvents);

        trips.forEach(trip -> {
            var seats = trip.getSeatsReserved();
            var seatsRemoved = tripMap.get(trip.getId());
            seats.removeAll(seatsRemoved);
            trip.setSeatsReserved(seats);
            if (trip.getSeatsAvailable() < trip.getTotalSeats()) {
                trip.setSeatsAvailable(trip.getSeatsAvailable() + seatsRemoved.size());
            }
        });
        tripRepository.saveAll(trips);
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


    public int plusMinus(List<Trip> trips, LocalDateTime dateTime, String scheduleId) {
        int result = 0;
        while (true) {
            LocalDateTime temp = dateTime;
            Trip trip = trips.stream().filter(t -> t.getStartTime().equals(temp) && t.getScheduleId().equals(scheduleId)).findFirst().orElse(null);
            if (trip == null) {
                break;
            } else {
                result += 60;
                dateTime = dateTime.plusMinutes(60);
            }
        }
        return result;
    }


    public List<AssignSchedule> createAssignSchedule(List<String> licensePlate, LocalDate startDate, LocalDate endDate, String route) {
        return licensePlate.stream().map(item -> AssignSchedule.builder()
                        .licensePlate(item)
                        .startDate(startDate)
                        .endDate(endDate)
                        .route(route)
                        .build())
                .collect(Collectors.toList());
    }

}
