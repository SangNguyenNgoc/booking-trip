package org.example.booking.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillCreate {
    private String tripId;
    private List<String> seats;
    private String passengerName;
    private String passengerPhone;
    private String passengerEmail;
}
