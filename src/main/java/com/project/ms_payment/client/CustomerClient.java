package com.project.ms_payment.client;

import com.project.ms_payment.dto.ChargeRequest;
import com.project.ms_payment.dto.ChargeResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-customer", url = "${clients.customer.base-url}")
public interface CustomerClient {
    @PostMapping("/api/v1/customers/internal/{customerId}/charge")
    ChargeResponse charge(@PathVariable Long customerId, @Valid @RequestBody ChargeRequest req);
}