package com.ride.payment.service;

import com.ride.payment.dto.*;
import com.ride.payment.entity.Payment;
import com.ride.payment.event.PaymentEvent;
import com.ride.payment.producer.PaymentEventProducer;
import com.ride.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentEventProducer producer;

    public PaymentResponseDTO pay(
            String userEmail,
            PaymentCreateDTO dto
    ) {

        if (repository.existsByRequestId(dto.getRequestId())) {
            throw new RuntimeException("Payment already done for this request");
        }

        Payment payment = Payment.builder()
                .requestId(dto.getRequestId())
                .userEmail(userEmail)
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod())
                .status(PaymentStatus.SUCCESS) // simulate success
                .createdAt(LocalDateTime.now())
                .build();

        Payment saved = repository.save(payment);

        // 🔥 Emit event
        producer.publish(
                PaymentEvent.builder()
                        .eventType("PAYMENT_SUCCESS")
                        .requestId(saved.getRequestId())
                        .userEmail(saved.getUserEmail())
                        .amount(saved.getAmount())
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        return PaymentResponseDTO.builder()
                .paymentId(saved.getId())
                .requestId(saved.getRequestId())
                .amount(saved.getAmount())
                .paymentMethod(saved.getPaymentMethod())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
