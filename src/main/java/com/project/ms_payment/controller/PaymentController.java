package com.project.ms_payment.controller;

import com.project.ms_payment.dto.PaymentCreateRequest;
import com.project.ms_payment.dto.PaymentResponse;
import com.project.ms_payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/charge")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse charge(@Valid @RequestBody PaymentCreateRequest req) {
        return paymentService.charge(req);
    }
}