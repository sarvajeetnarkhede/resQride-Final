package com.ride.servicerequest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ServiceRequestResponseDTO {

    private Long requestId;
    private String problemType;
    private String location;
    private Double amount;
    private boolean paid;
    private String userEmail;
    private LocalDateTime createdAt;
    private ServiceStatus status;
}
