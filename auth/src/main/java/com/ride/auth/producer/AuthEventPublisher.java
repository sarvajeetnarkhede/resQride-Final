package com.ride.auth.producer;

import com.ride.auth.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuthEventPublisher {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void userLoggedIn(String email) {
        kafkaTemplate.send(
                "user-events",
                email,
                NotificationEvent.builder()
                        .userEmail(email)
                        .role("USER")
                        .eventType("USER_LOGIN")
                        .message("User logged in successfully")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    public void userRegistered(String email) {
        kafkaTemplate.send(
                "user-events",
                email,
                NotificationEvent.builder()
                        .userEmail(email)
                        .role("USER")
                        .eventType("USER_REGISTER")
                        .message("Account created successfully")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}


