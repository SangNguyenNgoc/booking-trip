package org.example.booking.api.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.example.booking.api.dtos.BillCreate;
import org.example.booking.api.services.interfaces.BillService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
public class BillController {
    private final BillService billService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody BillCreate billCreate){
        return ResponseEntity.ok(billService.create(billCreate));
    }

    @GetMapping("/payment")
    @Hidden
    public ResponseEntity<String> handlePayment(
            @RequestParam("vnp_Amount") String amount,
            @RequestParam("vnp_BankCode") String bankCode,
            @RequestParam("vnp_BankTranNo") String bankTranNo,
            @RequestParam("vnp_CardType") String cardType,
            @RequestParam("vnp_OrderInfo") String orderInfo,
            @RequestParam("vnp_PayDate") String payDate,
            @RequestParam("vnp_ResponseCode") String responseCode,
            @RequestParam("vnp_TmnCode") String tmnCode,
            @RequestParam("vnp_TransactionNo") String transactionNo,
            @RequestParam("vnp_TransactionStatus") String transactionStatus,
            @RequestParam("vnp_TxnRef") String txnRef,
            @RequestParam("vnp_SecureHash") String secureHash
    ) {
        return ResponseEntity.ok(billService.payment(txnRef, responseCode, transactionStatus, payDate));
    }
}
