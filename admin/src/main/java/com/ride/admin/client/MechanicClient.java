package com.ride.admin.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;

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
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> Mono.just(java.util.List.of()))
                .block();
    }

    public Object getMechanicsBySkill(String skill) {
        return webClient.build()
                .get()
                .uri(SERVICE + "/api/mechanics/available/skill/{skill}", skill)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> Mono.just(java.util.List.of()))
                .block();
    }

    public Object getMechanicsBySkillWithDistance(String skill, Double latitude, Double longitude) {
        String uri = SERVICE + "/api/mechanics/available/skill/{skill}";
        if (latitude != null && longitude != null) {
            uri += "?latitude=" + latitude + "&longitude=" + longitude;
        }
        return webClient.build()
                .get()
                .uri(uri, skill)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> Mono.just(java.util.List.of()))
                .block();
    }

    public Object getNearestMechanics(Double latitude, Double longitude, Double maxDistance) {
        String uri = SERVICE + "/api/mechanics/available/nearest";
        if (latitude != null && longitude != null) {
            uri += "?latitude=" + latitude + "&longitude=" + longitude;
            if (maxDistance != null) {
                uri += "&maxDistance=" + maxDistance;
            }
        }
        return webClient.build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> Mono.just(java.util.List.of()))
                .block();
    }

    public Object allMechanics() {
        return webClient.build()
                .get()
                .uri(SERVICE + "/api/mechanics/all")
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> Mono.just(java.util.List.of()))
                .block();
    }

    public void verify(Long mechanicId) {
        webClient.build()
                .patch()
                .uri(SERVICE + "/api/mechanics/{id}/verify", mechanicId)
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> Mono.empty())
                .block();
    }
}
