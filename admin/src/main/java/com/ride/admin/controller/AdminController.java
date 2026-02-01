package com.ride.admin.controller;

import lombok.RequiredArgsConstructor;
import com.ride.admin.client.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ServiceRequestClient requestClient;
    private final MechanicClient mechanicClient;
    private final FeedbackClient feedbackClient;
    private final WebClient.Builder webClient;

    @GetMapping("/test")
    public Object test() {
        return java.util.Map.of(
            "message", "Admin service is working",
            "timestamp", java.time.Instant.now().toString(),
            "status", "OK"
        );
    }

    @GetMapping("/requests")
    public Object allRequests() {
        return requestClient.allRequests();
    }

    @GetMapping("/mechanics/available")
    public Object availableMechanics() {
        return mechanicClient.available();
    }

    @GetMapping("/mechanics/available/skill/{skill}")
    public Object getMechanicsBySkill(@PathVariable String skill) {
        return mechanicClient.getMechanicsBySkill(skill);
    }

    @GetMapping("/mechanics/available/skill/{skill}/nearest")
    public Object getMechanicsBySkillWithDistance(
            @PathVariable String skill,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {
        return mechanicClient.getMechanicsBySkillWithDistance(skill, latitude, longitude);
    }

    @GetMapping("/mechanics/nearest")
    public Object getNearestMechanics(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double maxDistance) {
        return mechanicClient.getNearestMechanics(latitude, longitude, maxDistance);
    }

    @GetMapping("/mechanics/skills")
    public String[] getAllSkills() {
        return new String[]{
            "GENERAL_MECHANIC",
            "TOWING", 
            "TIRE_SPECIALIST",
            "BATTERY_EXPERT",
            "LOCKSMITH"
        };
    }

    @GetMapping("/mechanics/all")
    public Object allMechanics() {
        return mechanicClient.allMechanics();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/assign")
    public void assign(
            @RequestParam Long requestId,
            @RequestParam Long mechanicId
    ) {
        requestClient.assignMechanic(requestId, mechanicId);
    }

    @PatchMapping("/mechanics/{id}/verify")
    public void verify(@PathVariable Long id) {
        mechanicClient.verify(id);
    }

    @GetMapping("/feedback/mechanic/{id}")
    public Object feedback(@PathVariable Long id) {
        return feedbackClient.byMechanic(id);
    }

    @GetMapping("/centers")
    public Object allCenters() {
        // Create a client to call the mechanic service for centers
        return webClient.build()
                .get()
                .uri("http://MECHANIC-SERVICE/api/centers")
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> Mono.just(java.util.List.of()))
                .block();
    }
}
