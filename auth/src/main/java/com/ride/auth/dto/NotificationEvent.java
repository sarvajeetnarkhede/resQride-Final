package com.ride.auth.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {
    private String userEmail;
    private String role;
    private String message;
    private String eventType;
    private LocalDateTime timestamp;
}