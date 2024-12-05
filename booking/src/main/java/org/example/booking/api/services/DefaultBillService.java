package org.example.booking.api.services;

import lombok.RequiredArgsConstructor;
import org.example.booking.api.dtos.*;
import org.example.booking.api.entities.Bill;
import org.example.booking.api.entities.BillStatus;
import org.example.booking.api.entities.Ticket;
import org.example.booking.api.entities.Trip;
import org.example.booking.api.repositories.BillRepository;
import org.example.booking.api.repositories.BillStatusRepository;
import org.example.booking.api.repositories.TicketRepository;
import org.example.booking.api.repositories.TripRepository;
import org.example.booking.api.services.interfaces.BillService;
import org.example.booking.api.services.mapper.BillMapper;
import org.example.booking.api.services.mapper.BillStatusMapper;
import org.example.booking.api.services.mapper.TripMapper;
import org.example.booking.api.specifications.BillSpecification;
import org.example.booking.clients.TripClient;
import org.example.booking.config.VariableConfig;
import org.example.booking.utils.dtos.ListResponse;
import org.example.booking.utils.exception.DataNotFoundException;
import org.example.booking.utils.exception.InputInvalidException;
import org.example.booking.utils.services.AppUtils;
import org.example.booking.utils.services.VnPayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultBillService implements BillService {

    private final BillRepository billRepository;
    private final BillStatusRepository billStatusRepository;
    private final TicketRepository ticketRepository;
    private final TripRepository tripRepository;
    private final VnPayService vnPayService;
    private final TripClient tripClient;
    private final TripMapper tripMapper;
    private final BillMapper billMapper;
    private final BillStatusMapper billStatusMapper;
    private final VariableConfig variableConfig;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${url.fe-url}")
    private String feUrl;

    private Map<String, String> getStringStringMap() {
        var responseCodeMessages = new HashMap<String, String>();
        responseCodeMessages.put("09", "Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng");
        responseCodeMessages.put("10", "Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần");
        responseCodeMessages.put("11", "Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.");
        responseCodeMessages.put("12", "Thẻ/Tài khoản của khách hàng bị khóa.");
        responseCodeMessages.put("24", "Khách hàng hủy giao dịch.");
        responseCodeMessages.put("51", "Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.");
        responseCodeMessages.put("65", "Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.");
        responseCodeMessages.put("75", "Ngân hàng thanh toán đang bảo trì.");
        responseCodeMessages.put("79", "KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch.");
        responseCodeMessages.put("99", "Lỗi không xác định.");
        return responseCodeMessages;
    }

    @Override
    public String create(BillCreate billCreate) {
        var trip = checkTripAndGetPrize(billCreate.getTrip().getTripId());
        //Check ghế đã đặt chưa
        checkSeatAreReserved(billCreate.getTrip().getSeats(), billCreate.getTrip().getTripId());
        BillStatus billStatus = billStatusRepository.findById(1).orElseThrow(
                () -> new DataNotFoundException(List.of("Status not found")));

        var totalPrice = (trip.getPrice() * billCreate.getTrip().getSeats().size());
        var tripBillId = AppUtils.getRandomNumber(12) + LocalDateTime.now();


        var newBill = Bill.builder()
                .id(tripBillId)
                .expireAt(LocalDateTime.now().plusMinutes(10))
                .status(billStatus)
                .passengerName(billCreate.getPassengerName())
                .passengerEmail(billCreate.getPassengerEmail())
                .passengerPhone(billCreate.getPassengerPhone())
                .totalPrice((long) totalPrice)
                .trip(trip)
                .build();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String userId = jwt.getClaim("sub");
            newBill.setProfileId(userId);
        }
        List<BillCreate.TripBooking> listTrip = new ArrayList<>();
        //Tạo vé
        var tickets = createTickets(trip, billCreate.getTrip().getSeats(), newBill);
        newBill.setTickets(tickets);
        if (billCreate.getRoundTrip() != null){
            var roundTrip = checkTripAndGetPrize(billCreate.getRoundTrip().getTripId());
            checkSeatAreReserved(billCreate.getRoundTrip().getSeats(), billCreate.getRoundTrip().getTripId());
            totalPrice = totalPrice + (roundTrip.getPrice() * billCreate.getRoundTrip().getSeats().size());
            var roundBillId = AppUtils.getRandomNumber(12) + LocalDateTime.now();
            var newRoundBill = Bill.builder()
                    .id(roundBillId)
                    .expireAt(LocalDateTime.now().plusMinutes(10))
                    .status(billStatus)
                    .passengerName(billCreate.getPassengerName())
                    .passengerEmail(billCreate.getPassengerEmail())
                    .passengerPhone(billCreate.getPassengerPhone())
                    .totalPrice((long) (roundTrip.getPrice() * billCreate.getRoundTrip().getSeats().size()))
                    .trip(roundTrip)
                    .parent(newBill)
                    .build();
            var roundTickets = createTickets(roundTrip, billCreate.getRoundTrip().getSeats(), newRoundBill);
            newRoundBill.setTickets(roundTickets);
            newBill.setRoundTrip(newRoundBill);
            listTrip.add(billCreate.getRoundTrip());
        }
        listTrip.add(billCreate.getTrip());
        String paymentUrl = vnPayService.doPost((long) totalPrice, tripBillId);
        newBill.setPaymentUrl(paymentUrl);
        billRepository.save(newBill);
        kafkaTemplate.send("BillIsBooked", listTrip);
        if(newBill.getProfileId() == null) kafkaTemplate.send("NotificationBillIsBooked", billMapper.billToBillResponse(newBill));
        return paymentUrl;
    }

    private void checkSeatAreReserved(List<String> seatNames, String tripId) {
        List<Ticket> ticketByTrip = ticketRepository.findByTripId(tripId, LocalDateTime.now());
        Set<String> seatIdsAreReserved = ticketByTrip.stream()
                .map(Ticket::getSeatName)
                .collect(Collectors.toSet());
        for (var seatId : seatNames) {
            if (seatIdsAreReserved.contains(seatId)) {
                throw new InputInvalidException(List.of("Seats are reserved"));
            }
        }
    }

    private Trip checkTripAndGetPrize(String tripId) {
        var trip = tripRepository.findById(tripId);
        return trip.orElseGet(() -> tripRepository.save(
                tripMapper.toEntity(tripClient.getTrip(tripId, variableConfig.TRIP_API_KEY)
                        .orElseThrow(() -> new DataNotFoundException(List.of("Trip not found"))))
        ));
    }

    private Set<Ticket> createTickets(Trip trip, List<String> seatNames, Bill bill) {
        return seatNames.stream()
                .map(seatName -> Ticket.builder()
                        .id(AppUtils.getRandomNumber(15))
                        .bill(bill)
                        .seatName(seatName)
                        .price(trip.getPrice())
                        .build())
                .collect(Collectors.toSet());
    }

    @Transactional
    @Override
    public String payment(String id, String responseCode, String transactionStatus, String paymentAt) {
        Bill bill = billRepository.findByIdAndStatusId(id, 1).orElse(null);
        if (bill == null) return feUrl;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime dateTime = LocalDateTime.parse(paymentAt, formatter);
        if (responseCode.equals("00") && transactionStatus.equals("00")) {
            BillStatus billStatus = billStatusRepository.findById(2).orElseThrow(
                    () -> new DataNotFoundException(List.of("Status not found")));
            if (bill.getFailure() != null) bill.setFailure(null);
            if (bill.getFailureReason() != null) bill.setFailureReason(null);
            bill.setStatus(billStatus);
            bill.setPaymentAt(dateTime);
            List<BillStatistics> billStatistics = new ArrayList<>(List.of(billMapper.toStatistics(bill)));
            if (bill.getRoundTrip() != null){
                bill.getRoundTrip().setPaymentAt(dateTime);
                bill.getRoundTrip().setStatus(billStatus);
                billStatistics.add(billMapper.toStatistics(bill.getRoundTrip()));
            }
            kafkaTemplate.send("BillCreated", billStatistics);
            kafkaTemplate.send("PaymentNotification", billMapper.billToBillResponse(bill));
            return feUrl + "/tai-khoan/hoa-don/" + bill.getId();
        } else {
            String message = getMessage(responseCode, transactionStatus);
            bill.setFailureReason(message);
            bill.setFailureAt(dateTime);
            bill.setFailure(true);
            return feUrl + "?error=" + message;
        }
    }

    private String getMessage(String responseCode, String transactionStatus) {
        Map<String, String> responseCodeMessages = getStringStringMap();
        if (responseCodeMessages.containsKey(responseCode)) {
            return responseCodeMessages.get(transactionStatus);
        }
        if (transactionStatus.equals("01")) {
            return "Chưa thanh toán";
        } else {
            return "Transaction Status invalid";
        }
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    protected void billExpiredTask() {
        var billIsExpired = billRepository.findByExpireAtAndStatus(LocalDateTime.now());
        var billStatus = billStatusRepository.findById(3).orElseThrow(
                ()->new DataNotFoundException(List.of("Not found Status")) );
        billIsExpired.ifPresent(bills -> {
            var expiredTrips = bills.stream()
                    .map(bill -> {
                        var tripIsExpired = BillIsExpired.builder()
                                .tripId(bill.getTrip().getId())
                                .build();
                        var seatNames = bill.getTickets()
                                .stream()
                                .map(Ticket::getSeatName)
                                .toList();
                        tripIsExpired.setSeats(seatNames);
                        bill.setStatus(billStatus);
                        return tripIsExpired;
                    })
                    .toList();
            if(!expiredTrips.isEmpty()) kafkaTemplate.send("BillIsExpired", expiredTrips);
        });
    }

    @Override
    public ListResponse<BillGeneral> getBillByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaim("sub");
        var bills = billRepository.findBillByProfileId(userId);
        return ListResponse.<BillGeneral>builder()
                .data(bills.stream()
                        .map(this::mapToBillGenerate)
                        .toList()
                )
                .size(bills.size())
                .build();
    }

    @Override
    public BillResponse getBillById(String id){
        Optional<Bill> result;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String userId = jwt.getClaim("sub");
            result = billRepository.findByIdAndProfileId(id, userId);
        }else {
            result = billRepository.findByIdAndProfileIdIsNull(id);
        }
        return billMapper.billToBillResponse(result
                .orElseThrow(() -> new DataNotFoundException(List.of("Not found bill with id " + id))));
    }

    @Override
    public BillResponse getBillByIdAdmin(String id) {
        var result = billRepository.findBillById(id)
                .orElseThrow(() -> new DataNotFoundException(List.of("Bill with id" + id + "not found")));
        return billMapper.billToBillResponse(result);
    }

    @Override
    public BillResponse getBillByIdAndPhoneNumber(String billId, String phoneNumber){
        var result = billRepository.findBillByPhoneNumberAndId(billId, phoneNumber).orElseThrow(
                () -> new DataNotFoundException(List.of("Data Invalid"))
        );
        return billMapper.billToBillResponse(result);
    }

    @Override
    public ListResponse<BillStatusResponse> getBillStatus(){
        var billStatuses = billStatusRepository.findAll();
        return ListResponse.<BillStatusResponse>builder()
                .data(billStatuses.stream()
                        .map(billStatusMapper::toResponse).
                        toList())
                .size(billStatuses.size())
                .build();
    }

    @Override
    public Page<BillGeneral> searchBill(
            LocalDateTime from,
            LocalDateTime to,
            Integer status,
            String phoneNumber,
            LocalDateTime tripFrom,
            LocalDateTime tripTo,
            Pageable pageable
    ){

        Specification<Bill> spec = Specification.where(BillSpecification.createdBetween(from, to))
                .and(BillSpecification.hasPhoneNumber(phoneNumber))
                .and(BillSpecification.hasStatus(status))
                .and(BillSpecification.hasTripStartTimeBetween(tripFrom, tripTo))
                .and(BillSpecification.parentIsNull());
        var bills = billRepository.findAll(spec, pageable);
        return bills.map(this::mapToBillGenerate);
    }

    private BillGeneral mapToBillGenerate(Bill bill){
        var billResult = billMapper.toBillGeneral(bill);
        if(bill.getRoundTrip() != null)
        {
            var trip = BillGeneral.TripGeneralDto.builder()
                    .id(bill.getTrip().getId())
                    .returnId(bill.getRoundTrip().getTrip().getId())
                    .locationFromName(bill.getTrip().getLocationFromName())
                    .locationToName(bill.getTrip().getLocationToName())
                    .regionFromName(bill.getTrip().getRegionFromName())
                    .regionToName(bill.getTrip().getRegionToName())
                    .startTime(bill.getTrip().getStartTime())
                    .returnTime(bill.getRoundTrip().getTrip().getStartTime())
                    .build();
            billResult.setTotalPrice(bill.getTotalPrice() + bill.getRoundTrip().getTotalPrice());
            billResult.setType("Khứ hồi");
            billResult.setTrip(trip);
        }else {
            var trip = BillGeneral.TripGeneralDto.builder()
                    .id(bill.getTrip().getId())
                    .returnId(null)
                    .locationFromName(bill.getTrip().getLocationFromName())
                    .locationToName(bill.getTrip().getLocationToName())
                    .regionFromName(bill.getTrip().getRegionFromName())
                    .regionToName(bill.getTrip().getRegionToName())
                    .startTime(bill.getTrip().getStartTime())
                    .returnTime(null)
                    .build();
            billResult.setType("Một chiều");
            billResult.setTrip(trip);
        }
        return billResult;
    }

}
