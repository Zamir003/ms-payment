package com.project.ms_payment.dto;

import java.math.BigDecimal;

public record PaymentEvent(
        Long paymentId,
        Long enrollmentId,
        Long customerId,
        BigDecimal amount,
        String currency,
        String payerEmail,
        String status // PAID or FAILED
) {}