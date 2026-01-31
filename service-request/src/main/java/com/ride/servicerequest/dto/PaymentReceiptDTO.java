package com.ride.servicerequest.dto;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReceiptDTO {
    
    private String receiptId;
    private Long requestId;
    private String transactionId;
    private LocalDateTime paymentDate;
    private Double amount;
    private String paymentMethod;
    private String paymentStatus;
    
    // Customer Information
    private String customerEmail;
    private String customerName;
    
    // Service Information
    private String problemType;
    private String location;
    private LocalDateTime serviceDate;
    
    // Mechanic Information
    private Long mechanicId;
    private String mechanicName;
    private String mechanicSkillType;
    
    // Transaction Details
    private String currency;
    private Double taxAmount;
    private Double totalAmount;
    private String description;
    
    // Receipt Metadata
    private LocalDateTime generatedAt;
    private String verificationCode;
}
