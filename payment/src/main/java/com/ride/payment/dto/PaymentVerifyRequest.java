package com.ride.payment.dto;

import lombok.Data;

@Data
public class PaymentVerifyRequest {
    private Long requestId;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
