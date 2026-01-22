package com.ride.payment.repository;

import com.ride.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByRequestId(Long requestId);
}
