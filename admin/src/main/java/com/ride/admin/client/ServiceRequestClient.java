package com.ride.admin.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ServiceRequestClient {

    private final WebClient.Builder webClient;

    private static final String SERVICE = "http://SERVICE-REQUEST-SERVICE";

    public Object allRequests() {
        return webClient.build()
                .get()
                .uri(SERVICE + "/api/requests")
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> Mono.just(java.util.List.of()))
                .block();
    }

    public void assignMechanic(Long requestId, Long mechanicId) {
        webClient.build()
                .patch()
                .uri(SERVICE + "/api/requests/{id}/assign?mechanicId={m}",
                        requestId, mechanicId)
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> Mono.empty())
                .block();
    }
}
