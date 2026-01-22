package com.ride.mechanic.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MechanicResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private String skillType;
    private Double rating;
    private AvailabilityStatus availability;
    private boolean verified;
}
