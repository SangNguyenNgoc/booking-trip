package org.example.booking.api.services.interfaces;

import org.example.booking.api.dtos.BillCreate;
import org.example.booking.api.dtos.BillGeneral;
import org.example.booking.api.dtos.BillResponse;
import org.example.booking.api.dtos.BillStatusResponse;
import org.example.booking.utils.dtos.ListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface BillService {
    String create(BillCreate billCreate);
    String payment(String id, String responseCode, String transactionStatus, String paymentAt);
    ListResponse<BillGeneral> getBillByUser();
    BillResponse getBillByIdAdmin(String id);
    public BillResponse getBillByIdAndPhoneNumber(String billId, String phoneNumber);
    BillResponse getBillById(String id);
    ListResponse<BillStatusResponse> getBillStatus();
    Page<BillGeneral> searchBill(
            LocalDateTime from,
            LocalDateTime to,
            Integer status,
            String phoneNumber,
            LocalDateTime tripFrom,
            LocalDateTime tripTo,
            Pageable pageable
    );
}
