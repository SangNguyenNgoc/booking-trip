package org.example.booking.api.dtos;

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
public class BillGeneral {
    String id;
    LocalDateTime paymentAt;
    LocalDateTime expireAt;
    Long totalPrice;
    String paymentUrl;
    String passengerName;
    String passengerPhone;
    String passengerEmail;
    String status;
    String type;
    TripGeneralDto trip;
    LocalDateTime createDate;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TripGeneralDto implements Serializable {
        String id;
        String returnId;
        LocalDateTime startTime;
        LocalDateTime returnTime;
        String regionFromName;
        String regionToName;
        String locationFromName;
        String locationToName;
    }
}
