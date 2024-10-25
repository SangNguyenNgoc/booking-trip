package org.example.booking.api.services.interfaces;

import org.example.booking.api.dtos.BillCreate;
import org.example.booking.api.dtos.BillResponse;
import org.example.booking.utils.dtos.ListResponse;

public interface BillService {
    String create(BillCreate billCreate);
    String payment(String id, String responseCode, String transactionStatus, String paymentAt);
    ListResponse<BillResponse> getBillByUser();
    ListResponse<BillResponse> getBillByPhoneNumber(String phoneNumber);
}
