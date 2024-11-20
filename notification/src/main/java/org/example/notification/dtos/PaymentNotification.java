package org.example.notification.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentNotification implements Serializable {
    String id;
    LocalDateTime paymentAt;
    LocalDateTime expireAt;
    Long totalPrice;
    String paymentUrl;
    String failureReason;
    LocalDateTime failureAt;
    Boolean failure;
    String passengerName;
    String passengerPhone;
    String passengerEmail;
    Set<TicketDto> tickets;
    BillStatusDto status;
    TripDto trip;
    PaymentNotification roundTrip;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketDto implements Serializable {
        String id;
        String seatName;
        Double price;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillStatusDto implements Serializable {
        String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TripDto implements Serializable {
        String id;
        LocalDateTime startTime;
        LocalDateTime endTime;
        String startAt;
        String endAt;
        String regionFromName;
        String regionToName;
        String locationFromName;
        String locationToName;
    }
}