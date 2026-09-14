package com.project.ms_payment.service;

import com.project.ms_payment.client.CustomerClient;
import com.project.ms_payment.config.RabbitConfig;
import com.project.ms_payment.dto.*;
import com.project.ms_payment.model.Payment;
import com.project.ms_payment.model.PaymentStatus;
import com.project.ms_payment.model.RecordStatus;
import com.project.ms_payment.repo.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerClient customerClient;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public PaymentResponse charge(PaymentCreateRequest req) {
        Payment p = new Payment();
        p.setEnrollmentId(req.enrollmentId());
        p.setCustomerId(req.customerId());
        p.setAmount(req.amount());
        p.setCurrency(req.currency());
        p.setPayerEmail(req.payerEmail());
        p.setStatus(PaymentStatus.PENDING);
        p.setRecordStatus(RecordStatus.ACTIVE);

        Payment saved = paymentRepository.save(p);

        ChargeResponse charge = customerClient.charge(req.customerId(), new ChargeRequest(req.amount(), req.currency()));

        if (charge.ok()) {
            saved.setStatus(PaymentStatus.PAID);
            paymentRepository.save(saved);

            publish(saved, "payment.succeeded");
            return new PaymentResponse(saved.getId(), "PAID");
        } else {
            saved.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(saved);

            publish(saved, "payment.failed");
            return new PaymentResponse(saved.getId(), "FAILED");
        }
    }

    private void publish(Payment p, String routingKey) {
        PaymentEvent event = new PaymentEvent(
                p.getId(),
                p.getEnrollmentId(),
                p.getCustomerId(),
                p.getAmount(),
                p.getCurrency(),
                p.getPayerEmail(),
                p.getStatus().name()
        );
        rabbitTemplate.convertAndSend(RabbitConfig.PAYMENTS_EXCHANGE, routingKey, event);
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentEvent> list(int page, int size) {
        var p = paymentRepository.findAllByRecordStatusNot(RecordStatus.DELETED, PageRequest.of(page, size));
        List<PaymentEvent> items = p.getContent().stream()
                .map(x -> new PaymentEvent(x.getId(), x.getEnrollmentId(), x.getCustomerId(),
                        x.getAmount(), x.getCurrency(), x.getPayerEmail(), x.getStatus().name()))
                .toList();
        return new PageResponse<>(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    public record PageResponse<T>(List<T> items, int page, int size, long totalElements, int totalPages) {}
}