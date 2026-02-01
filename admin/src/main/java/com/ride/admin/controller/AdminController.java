package com.ride.admin.controller;

import lombok.RequiredArgsConstructor;
import com.ride.admin.client.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ServiceRequestClient requestClient;
    private final MechanicClient mechanicClient;
    private final FeedbackClient feedbackClient;


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
}
