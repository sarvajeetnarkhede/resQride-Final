package com.ride.servicerequest.service;

import com.ride.servicerequest.dto.*;
import com.ride.servicerequest.entity.ServiceRequest;
import com.ride.servicerequest.repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceRequestService {

    private final ServiceRequestRepository repository;

    // CREATE REQUEST
    public ServiceRequestResponseDTO create(
            String userEmail,
            ServiceRequestCreateDTO dto
    ) {

        ServiceRequest request = ServiceRequest.builder()
                .userEmail(userEmail)
                .location(dto.getLocation())
                .problemType(dto.getProblemType())
                .amount(dto.getAmount())
                .paid(false)
                .status(ServiceStatus.PAYMENT_PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        ServiceRequest saved = repository.save(request);
        return map(saved);
    }

    // MARK PAID (Kafka event will call this)
    public void markPaid(Long requestId) {

        ServiceRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        if (request.isPaid()) return; // idempotency

        request.setPaid(true);
        request.setStatus(ServiceStatus.PAID);
        repository.save(request);
    }

    // USER REQUESTS
    public List<ServiceRequestResponseDTO> getMyRequests(String email) {
        return repository.findByUserEmail(email)
                .stream()
                .map(this::map)
                .toList();
    }

    // GET ALL REQUESTS (ADMIN ONLY)
    public List<ServiceRequestResponseDTO> getAllRequests() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    private ServiceRequestResponseDTO map(ServiceRequest r) {
        return ServiceRequestResponseDTO.builder()
                .requestId(r.getId())
                .problemType(r.getProblemType())
                .location(r.getLocation())
                .amount(r.getAmount())
                .paid(r.isPaid())
                .status(r.getStatus())
                .userEmail(r.getUserEmail())  // Include user email in the response
                .createdAt(r.getCreatedAt())  // Include creation timestamp
                .build();
    }
}
