package com.ride.servicerequest.controller;

import com.ride.servicerequest.dto.*;
import com.ride.servicerequest.service.ServiceRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@Slf4j
public class ServiceRequestController {

    private final ServiceRequestService service;

    @PostMapping
    public ServiceRequestResponseDTO create(
            Authentication auth,
            @RequestBody ServiceRequestCreateDTO dto
    ) {
        log.info("Received request to create service request for user: {}", 
                auth != null ? auth.getName() : "unknown");
        
        if (auth == null || auth.getName() == null) {
            log.error("Authentication missing for create request");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        
        return service.create(auth.getName(), dto);
    }

    @GetMapping("/me")
    public List<ServiceRequestResponseDTO> myRequests(
            Authentication auth
    ) {
        log.info("Fetching requests for authenticated user: {}", 
                auth != null ? auth.getName() : "null");

        /**
         * ROOT CAUSE ANALYSIS:
         * The 500 Internal Server Error was caused by a NullPointerException when calling auth.getName().
         * This occurred because the Gateway's JwtAuthFilter incorrectly marked /api/requests/me as a public route,
         * causing it to skip JWT validation and header injection (X-User-Email).
         * 
         * SOLUTION:
         * 1. Added null checks for the Authentication object to handle cases where headers are missing gracefully.
         * 2. (In Gateway) Corrected JwtAuthFilter to ensure /api/requests/me is treated as a protected route.
         * 3. Added logging and proper exception handling (401 Unauthorized) to improve diagnosability.
         */
        if (auth == null || auth.getName() == null) {
            log.error("Access denied to /me: Authentication object is null. Check if Gateway is injecting headers.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authentication credentials");
        }

        try {
            List<ServiceRequestResponseDTO> requests = service.getMyRequests(auth.getName());
            log.info("Found {} requests for user {}", requests.size(), auth.getName());
            return requests;
        } catch (Exception e) {
            log.error("Error retrieving requests for user {}: {}", auth.getName(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving requests", e);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ServiceRequestResponseDTO> getAllRequests(Authentication auth) {
        log.info("Admin fetching all service requests");
        return service.getAllRequests();
    }


}
