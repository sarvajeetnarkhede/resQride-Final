package com.ride.location.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LocationResponseDTO {
    private Long id;
    private String userEmail;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
}
