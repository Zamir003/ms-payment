package com.project.ms_payment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payments")
@SQLDelete(sql = "UPDATE payments SET record_status='DELETED' WHERE id=?")
@SQLRestriction("record_status <> 'DELETED'")
public class Payment extends BaseEntity {

    @Column(nullable = false)
    private Long enrollmentId;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String payerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "record_status")
    private RecordStatus recordStatus = RecordStatus.ACTIVE;
}
