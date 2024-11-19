package org.example.booking.api.dtos;

import java.time.LocalDateTime;

public class BillStatistics {
    private String id;
    private String tripId;
    private Long totalPrice;
    private Integer totalTicket;
    private LocalDateTime createDate;
}
