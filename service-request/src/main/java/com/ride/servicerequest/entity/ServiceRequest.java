package com.ride.servicerequest.entity;

import jakarta.persistence.*;
import lombok.*;
import com.ride.servicerequest.dto.ServiceStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // injected by gateway
    private String userEmail;

    // assigned later by admin
    private String mechanicEmail;

    private String location;

    private String problemType;

    // 💰 NEW
    private Double amount;

    // 💳 NEW
    private boolean paid;

    @Enumerated(EnumType.STRING)
    private ServiceStatus status;

    private LocalDateTime createdAt;
}
