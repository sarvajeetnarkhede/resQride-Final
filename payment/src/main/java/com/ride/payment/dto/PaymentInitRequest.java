package com.ride.payment.dto;

import lombok.Data;

@Data
public class PaymentInitRequest {
    private Long requestId;
    private Double amount;
    private String currency;
}
