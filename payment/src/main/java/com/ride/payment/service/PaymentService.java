package com.ride.payment.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.ride.payment.dto.*;
import com.ride.payment.entity.Payment;
import com.ride.payment.event.PaymentEvent;
import com.ride.payment.producer.PaymentEventProducer;
import com.ride.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentEventProducer producer;
    private final WebClient.Builder webClient;

    @Value("${razorpay.key}")
    private String keyId;

    @Value("${razorpay.secret}")
    private String keySecret;

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

    public Map<String, Object> createOrder(PaymentInitRequest request) {
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            
            JSONObject orderRequest = new JSONObject();
            // Convert amount to paise (100 paise = 1 INR)
            orderRequest.put("amount", (int)(request.getAmount() * 100)); 
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + request.getRequestId());
            Order order = client.orders.create(orderRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", "INR");
            response.put("key", keyId); // Frontend needs this
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error creating Razorpay order", e);
        }
    }

    public void verifyPayment(PaymentVerifyRequest request) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());
            
            boolean valid = Utils.verifyPaymentSignature(options, keySecret);
            
            if (valid) {
                // TODO: Update your database status to PAID
                // paymentRepository.save(...);
                // requestClient.updateStatus(request.getRequestId(), "PAID");
                
                // Create payment record
                Payment payment = Payment.builder()
                        .requestId(request.getRequestId())
                        .razorpayOrderId(request.getRazorpayOrderId())
                        .razorpayPaymentId(request.getRazorpayPaymentId())
                        .razorpaySignature(request.getRazorpaySignature())
                        .status(PaymentStatus.SUCCESS)
                        .paymentMethod("RAZORPAY")
                        .createdAt(LocalDateTime.now())
                        .build();
                
                repository.save(payment);
                
                // Emit payment success event
                producer.publish(
                        PaymentEvent.builder()
                                .eventType("PAYMENT_SUCCESS")
                                .requestId(request.getRequestId())
                                .amount(null) // Will be populated from order if needed
                                .timestamp(LocalDateTime.now())
                                .build()
                );
                
                // Update service request status to PAID
                try {
                    webClient.build()
                            .patch()
                            .uri("http://SERVICE-REQUEST-SERVICE/api/requests/" + request.getRequestId() + "/status?status=PAID")
                            .retrieve()
                            .toBodilessEntity()
                            .timeout(java.time.Duration.ofSeconds(5))
                            .onErrorResume(e -> {
                                System.err.println("Failed to update request status: " + e.getMessage());
                                return reactor.core.publisher.Mono.empty();
                            })
                            .block();
                } catch (Exception e) {
                    System.err.println("Error updating service request status: " + e.getMessage());
                }
                
            } else {
                throw new RuntimeException("Payment signature verification failed");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error verifying payment", e);
        }
    }
}
