package com.ride.servicerequest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceRequestResponseDTO {

    private Long requestId;
    private String problemType;
    private String location;
    private Double amount;
    private boolean paid;
    private ServiceStatus status;
}
