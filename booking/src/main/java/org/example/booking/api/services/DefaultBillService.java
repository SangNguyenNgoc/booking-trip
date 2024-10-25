package org.example.booking.api.services;

import lombok.RequiredArgsConstructor;
import org.example.booking.api.dtos.BillCreate;
import org.example.booking.api.dtos.BillResponse;
import org.example.booking.api.dtos.BillIsExpired;
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
import org.example.booking.api.services.mapper.TripMapper;
import org.example.booking.clients.TripClient;
import org.example.booking.config.VariableConfig;
import org.example.booking.utils.dtos.ListResponse;
import org.example.booking.utils.exception.DataNotFoundException;
import org.example.booking.utils.exception.InputInvalidException;
import org.example.booking.utils.services.AppUtils;
import org.example.booking.utils.services.VnPayService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
    private final VariableConfig variableConfig;
    private final KafkaTemplate<String, Object> kafkaTemplate;

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
        var trip = checkTripAndGetPrize(billCreate.getTripId());
        //Check ghế đã đặt chưa
        checkSeatAreReserved(billCreate.getSeatName(), billCreate.getTripId());
        BillStatus billStatus = billStatusRepository.findById(1).orElseThrow(
                () -> new DataNotFoundException(List.of("Status not found")));

        var totalPrice = trip.getPrice() * billCreate.getSeatName().size();
        var billId = AppUtils.getRandomNumber(12) + LocalDateTime.now();
        String paymentUrl = vnPayService.doPost((long) totalPrice, billId);
        var newBill = Bill.builder()
                .id(billId)
                .expireAt(LocalDateTime.now().plusMinutes(10))
                .status(billStatus)
                .passengerName(billCreate.getPassengerName())
                .passengerEmail(billCreate.getPassengerEmail())
                .passengerPhone(billCreate.getPassengerPhone())
                .totalPrice((long) totalPrice)
                .paymentUrl(paymentUrl)
                .trip(trip)
                .build();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String userId = jwt.getClaim("sub");
            newBill.setProfileId(userId);
        }

        kafkaTemplate.send("BillIsBooked", billCreate);
        //Tạo vé
        var tickets = createTickets(trip, billCreate.getSeatName(), newBill);
        newBill.setTickets(tickets);
        billRepository.save(newBill);
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
        Bill bill = billRepository.findByIdAndStatusId(id, 1).orElseThrow(
                () -> new DataNotFoundException(List.of("Bill not found"))
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime dateTime = LocalDateTime.parse(paymentAt, formatter);
        if (responseCode.equals("00") && transactionStatus.equals("00")) {
            BillStatus billStatus = billStatusRepository.findById(2).orElseThrow(
                    () -> new DataNotFoundException(List.of("Status not found")));
            if (bill.getFailure() != null) bill.setFailure(null);
            if (bill.getFailureReason() != null) bill.setFailureReason(null);
            bill.setStatus(billStatus);
            bill.setPaymentAt(dateTime);

//            kafkaTemplate.send("TripIsPaid", tripIsPaid);
            return "Success";
        } else {
            String message = getMessage(responseCode, transactionStatus);
            bill.setFailureReason(message);
            bill.setFailureAt(dateTime);
            bill.setFailure(true);
            return message;
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
                        tripIsExpired.setSeatName(seatNames);
                        return tripIsExpired;
                    })
                    .toList();
            kafkaTemplate.send("BillIsExpired", expiredTrips);
        });
    }

    @Override
    public ListResponse<BillResponse> getBillByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaim("sub");
        var result = billRepository.findBillByProfileId(userId);
        return ListResponse.<BillResponse>builder()
                .data(result.stream()
                        .map(billMapper::toDto)
                        .toList()
                )
                .size(result.size())
                .build();
    }

    @Override
    public ListResponse<BillResponse> getBillByPhoneNumber(String phoneNumber) {
        var result = billRepository.findBillByPhoneNumber(phoneNumber);
        return ListResponse.<BillResponse>builder()
                .data(result.stream()
                        .map(billMapper::toDto)
                        .toList()
                )
                .size(result.size())
                .build();
    }

//    @Transactional
//    @KafkaListener(
//            topics = "",
//            id = "billError"
//    )
//    protected void billFailed(){
//
//    }

}
