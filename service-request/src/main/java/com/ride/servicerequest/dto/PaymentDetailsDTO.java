package com.ride.servicerequest.dto;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailsDTO {
    
    private Long requestId;
    private String problemType;
    private String location;
    private Double amount;
    private String mechanicName;
    private String status;
    private String message;
}
