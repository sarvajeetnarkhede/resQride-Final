package com.ride.payment.dto;

import lombok.Data;

@Data
public class PaymentCreateDTO {

    private Long requestId;
    private Double amount;
    private String paymentMethod;
}
