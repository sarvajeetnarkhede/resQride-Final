package com.ride.mechanic.controller;

import lombok.RequiredArgsConstructor;
import com.ride.mechanic.dto.*;
import com.ride.mechanic.service.MechanicService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/mechanics")
@RequiredArgsConstructor
public class MechanicController {

    private final MechanicService service;

    @PostMapping("/register")

    public Mono<MechanicResponseDTO> register(@RequestBody MechanicCreateDTO dto) {
        return service.register(dto);
    }

    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public void verify(@PathVariable Long id) {
        service.verify(id);
    }

    @PatchMapping("/availability")
    @PreAuthorize("hasRole('MECHANIC')")
    public void availability(
            Authentication auth,
            @RequestParam AvailabilityStatus status
    ) {
        service.updateAvailability(auth.getName(), status);
    }

    @GetMapping("/available")
    public List<MechanicResponseDTO> available() {
        return service.available();
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('MECHANIC')")
    public List<Object> getMyRequests(Authentication auth) {
        return service.getMyRequests(auth.getName());
    }
}
