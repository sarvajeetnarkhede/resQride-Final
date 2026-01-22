package com.ride.admin.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class FeedbackClient {

    private final WebClient.Builder webClient;

    private static final String SERVICE = "http://FEEDBACK-SERVICE";

    public Object byMechanic(Long mechanicId) {
        return webClient.build()
                .get()
                .uri(SERVICE + "/api/feedback/mechanic/{id}", mechanicId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }
}
