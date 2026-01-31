package com.ride.mechanic.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceRequestClient {

    private final WebClient.Builder webClient;

    private static final String SERVICE = "http://SERVICE-REQUEST-SERVICE";

    public List<Object> getMyRequests(Long mechanicId) {
        return webClient.build()
                .get()
                .uri(SERVICE + "/api/requests/mechanic/{mechanicId}", mechanicId)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .block();
    }
}
