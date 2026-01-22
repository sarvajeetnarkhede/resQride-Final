package com.ride.location.controller;

import lombok.RequiredArgsConstructor;
import com.ride.location.dto.LocationCreateDTO;
import com.ride.location.service.LocationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService service;

    @PostMapping
    public Object log(
            Authentication auth,
            @RequestBody LocationCreateDTO dto
    ) {
        return service.log(auth.getName(), dto);
    }

    @GetMapping("/me")
    public Object myHistory(Authentication auth) {
        return service.history(auth.getName());
    }
}
