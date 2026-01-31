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

    @GetMapping("/all")
    public List<MechanicResponseDTO> getAllMechanics() {
        return service.getAllMechanics();
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('MECHANIC')")
    public List<Object> getMyRequests(Authentication auth) {
        return service.getMyRequests(auth.getName());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MECHANIC')")
    public MechanicResponseDTO getMyProfile(Authentication auth) {
        return service.getMechanicByEmail(auth.getName());
    }

    @GetMapping("/{id}/name")
    public String getMechanicName(@PathVariable Long id) {
        return service.getMechanicName(id);
    }


    @GetMapping("/{id}/email")
    public String getMechanicEmail(@PathVariable Long id) {
        return service.getMechanicEmail(id);
    }

    @PatchMapping("/{id}/rating")
    public void updateRating(@PathVariable Long id, @RequestBody Double newRating) {
        service.updateRating(id, newRating);
    }

    @PatchMapping("/{id}/availability")
    public void updateAvailability(@PathVariable Long id, @RequestParam AvailabilityStatus status) {
        service.updateAvailabilityById(id, status);
    }

    @GetMapping("/{id}/details")
    public MechanicResponseDTO getMechanicDetails(@PathVariable Long id) {
        return service.getMechanicById(id);
    }
}
