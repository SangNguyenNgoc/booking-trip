package org.example.booking.api.dtos;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse implements Serializable {
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
    String status;
    TripDto trip;
    BillResponse roundTrip;
    LocalDateTime createDate;

    /**
     * DTO for {@link org.example.booking.api.entities.Ticket}
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketDto implements Serializable {
        String id;
        String seatName;
        Double price;
    }

    /**
     * DTO for {@link org.example.booking.api.entities.Trip}
     */
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