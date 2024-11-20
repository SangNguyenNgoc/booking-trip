package org.example.booking.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillStatistics implements Serializable {
    private String id;
    private String tripId;
    private Long totalPrice;
    private Integer totalTicket;
    private LocalDateTime createDate;
}
