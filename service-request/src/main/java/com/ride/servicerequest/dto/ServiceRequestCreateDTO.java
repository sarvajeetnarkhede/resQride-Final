package com.ride.servicerequest.dto;

import lombok.Data;

@Data
public class ServiceRequestCreateDTO {

    private String location;
    private String problemType;
    private Double amount;
}
