package com.ride.mechanic.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FeedbackClient {

    private final WebClient.Builder webClientBuilder;
    private static final String FEEDBACK_SERVICE = "http://FEEDBACK-SERVICE";

    public Mono<Double> getAverageRating(Long mechanicId) {
        return webClientBuilder.build()
                .get()
                .uri(FEEDBACK_SERVICE + "/api/feedback/mechanic/" + mechanicId + "/rating")
                .retrieve()
                .bodyToMono(Double.class)
                .onErrorReturn(0.0); // Fallback if feedback service is unavailable
    }
}
