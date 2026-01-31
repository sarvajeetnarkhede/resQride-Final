package com.ride.servicerequest.client;

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

    public Mono<String> getMechanicName(Long mechanicId) {
        return webClientBuilder.build()
                .get()
                .uri(MECHANIC_SERVICE + "/api/mechanics/" + mechanicId + "/name")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5)) // Add 5 second timeout
                .onErrorReturn("Unknown Mechanic"); // Fallback if mechanic service is unavailable
    }

    public Mono<String> getMechanicEmail(Long mechanicId) {
        return webClientBuilder.build()
                .get()
                .uri(MECHANIC_SERVICE + "/api/mechanics/" + mechanicId + "/email")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5)) // Add 5 second timeout
                .onErrorReturn("unknown@example.com"); // Fallback if mechanic service is unavailable
    }

    public Mono<Void> updateAvailability(Long mechanicId, String status) {
        System.out.println("Attempting to update availability for mechanic " + mechanicId + " to status: " + status);
        return webClientBuilder.build()
                .patch()
                .uri(MECHANIC_SERVICE + "/api/mechanics/" + mechanicId + "/availability?status=" + status)
                .retrieve()
                .bodyToMono(Void.class)
                .timeout(Duration.ofSeconds(5))
                .doOnSuccess(v -> System.out.println("Successfully updated mechanic availability"))
                .doOnError(error -> System.err.println("Failed to update mechanic availability: " + error.getMessage()))
                .onErrorResume(error -> {
                    System.err.println("Error details: " + error.getClass().getSimpleName() + " - " + error.getMessage());
                    return Mono.empty();
                });
    }
}
