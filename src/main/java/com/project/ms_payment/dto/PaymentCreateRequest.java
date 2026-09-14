package com.project.ms_payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentCreateRequest(
        @NotNull Long enrollmentId,
        @NotNull Long customerId,
        @NotNull BigDecimal amount,
        @NotBlank String currency,
        @NotBlank String payerEmail
) {}