package org.example.booking.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    BillResponse.BillStatusDto status;
}
