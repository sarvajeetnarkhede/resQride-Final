package com.ride.auth.client;

import com.ride.auth.dto.InternalUserAuthResponse;
import com.ride.auth.dto.UserRegistrationRequest;
import com.ride.auth.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;
    private static final String USER_SERVICE = "http://USER-SERVICE";  // Back to service discovery

    public Mono<InternalUserAuthResponse> getUserForAuth(String email) {
        return webClientBuilder.build()
                .get()
                .uri(USER_SERVICE + "/api/internal/users/auth/{email}", email)
                .retrieve()
                .bodyToMono(InternalUserAuthResponse.class);
    }

    public Mono<UserResponse> createUser(UserRegistrationRequest request) {
        return webClientBuilder.build()
                .post()
                .uri(USER_SERVICE + "/api/internal/users")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class);
    }
}

