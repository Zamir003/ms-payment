package com.project.ms_payment.repo;

import com.project.ms_payment.model.Payment;
import com.project.ms_payment.model.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findAllByRecordStatusNot(RecordStatus status, Pageable pageable);
}