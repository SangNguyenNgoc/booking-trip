package org.example.booking.api.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.booking.api.dtos.BillCreate;
import org.example.booking.api.dtos.BillGeneral;
import org.example.booking.api.dtos.BillResponse;
import org.example.booking.api.services.interfaces.BillService;
import org.example.booking.utils.dtos.ListResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {
    private final BillService billService;

    @Operation(
            summary = "Create payment url",
            description = "This endpoint allows customer book ticket and pay bill",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping
    public ResponseEntity<String> create(@RequestBody BillCreate billCreate){
        return ResponseEntity.ok(billService.create(billCreate));
    }

    @GetMapping("/payment")
    @Hidden
    public ResponseEntity<Void> handlePayment(
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
        String redirectUrl = billService.payment(txnRef, responseCode, transactionStatus, payDate);

        // Tạo header Location để chuyển hướng
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUrl));

        // Trả về phản hồi với mã trạng thái 302 (Found)
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }

    @Operation(
            summary = "Get all bill was created by logged-in account",
            description = "This endpoint allows logged-in account get all bill was created",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ListResponse<BillGeneral>> getBillsByUser(){
        return ResponseEntity.ok(billService.getBillByUser());
    }

    @Operation(
            summary = "Get all bill was created with phone number",
            description = "This endpoint allows admin get all bill was created with phone number",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("admin/search")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<ListResponse<BillResponse>> getBillsByQuery(@RequestParam("phoneNumber") String phoneNumber){
        return ResponseEntity.ok(billService.getBillByPhoneNumber(phoneNumber));
    }

    @Operation(
            summary = "Get bill was created by id",
            description = "This endpoint allows get bill was created by id",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("user/search")
    public ResponseEntity<BillResponse> getBillsById(
            @RequestParam("billId") String billId,
            @RequestParam("phoneNumber") String phoneNumber
    ){
        return ResponseEntity.ok(billService.getBillByIdAndPhoneNumber(billId, phoneNumber));
    }

    @Operation(
            summary = "Get bill was created by id",
            description = "This endpoint allows get bill was created by id",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/{id}")
    public ResponseEntity<BillResponse> getById(@PathVariable String id){
        return ResponseEntity.ok(billService.getBillById(id));
    }
}
