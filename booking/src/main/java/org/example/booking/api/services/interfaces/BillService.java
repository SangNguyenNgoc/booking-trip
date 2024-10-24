package org.example.booking.api.services.interfaces;

import org.example.booking.api.dtos.BillCreate;

public interface BillService {
    String create(BillCreate billCreate);
    String payment(String id, String responseCode, String transactionStatus, String paymentAt);
}
