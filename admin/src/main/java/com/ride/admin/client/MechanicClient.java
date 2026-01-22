package com.ride.admin.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class MechanicClient {

    private final WebClient.Builder webClient;

    private static final String SERVICE = "http://MECHANIC-SERVICE";

    public Object available() {
        return webClient.build()
                .get()
                .uri(SERVICE + "/api/mechanics/available")
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public void verify(Long mechanicId) {
        webClient.build()
                .patch()
                .uri(SERVICE + "/api/mechanics/{id}/verify", mechanicId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
