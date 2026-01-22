package com.ride.servicerequest.consumer;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {

    private String eventType;
    private Long requestId;
    private String userEmail;
    private Double amount;
    private LocalDateTime timestamp;
}
