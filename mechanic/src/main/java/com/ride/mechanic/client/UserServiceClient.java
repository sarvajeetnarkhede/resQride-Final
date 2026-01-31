package com.ride.mechanic.client;

import com.ride.mechanic.dto.UserCreateRequest;
import com.ride.mechanic.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;
    private static final String USER_SERVICE = "http://USER-SERVICE";

    @Autowired
    public UserServiceClient(@LoadBalanced WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<UserResponse> createUser(UserCreateRequest request) {
        return webClientBuilder.build()
                .post()
                .uri(USER_SERVICE + "/api/internal/users")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class);
    }
}
