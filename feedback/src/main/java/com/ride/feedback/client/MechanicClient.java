package com.ride.feedback.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MechanicClient {

    private final WebClient.Builder webClientBuilder;
    private static final String MECHANIC_SERVICE = "http://MECHANIC-SERVICE";

    public Mono<Void> updateMechanicRating(Long mechanicId, Double newRating) {
        return webClientBuilder.build()
                .patch()
                .uri(MECHANIC_SERVICE + "/api/mechanics/" + mechanicId + "/rating")
                .bodyValue(newRating)
                .retrieve()
                .bodyToMono(Void.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(error -> {
                    System.err.println("Failed to update mechanic rating: " + error.getMessage());
                    return Mono.empty();
                });
    }
}
