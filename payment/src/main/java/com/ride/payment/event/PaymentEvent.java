package com.ride.payment.event;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {

    private String eventType; // PAYMENT_SUCCESS
    private Long requestId;
    private String userEmail;
    private Double amount;
    private LocalDateTime timestamp;
}
