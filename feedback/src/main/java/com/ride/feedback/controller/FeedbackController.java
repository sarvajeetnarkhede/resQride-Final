package com.ride.feedback.controller;

import lombok.RequiredArgsConstructor;
import com.ride.feedback.dto.FeedbackCreateDTO;
import com.ride.feedback.service.FeedbackService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService service;

    @PostMapping
    public Object submit(
            Authentication auth,
            @RequestBody FeedbackCreateDTO dto
    ) {
        return service.create(auth.getName(), dto);
    }

    @PutMapping
    public Object update(
            Authentication auth,
            @RequestBody FeedbackCreateDTO dto
    ) {
        return service.update(auth.getName(), dto);
    }

    @GetMapping("/mechanic/{id}")
    public Object byMechanic(@PathVariable Long id) {
        return service.byMechanic(id);
    }

    @GetMapping("/request/{id}")
    public Object byRequest(@PathVariable Long id) {
        return service.byRequest(id);
    }

    @GetMapping("/mechanic/{id}/rating")
    public Double getAverageRating(@PathVariable Long id) {
        return service.getAverageRatingForMechanic(id);
    }
}
