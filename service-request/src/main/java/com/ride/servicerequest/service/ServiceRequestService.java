package com.ride.servicerequest.service;

import com.ride.servicerequest.client.MechanicClient;
import com.ride.servicerequest.dto.*;
import com.ride.servicerequest.entity.ServiceRequest;
import com.ride.servicerequest.repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ServiceRequestService {

    private final ServiceRequestRepository repository;
    private final MechanicClient mechanicClient;

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
                .status(ServiceStatus.CREATED)
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

    // ASSIGN MECHANIC (ADMIN ONLY)
    public void assignMechanic(Long requestId, Long mechanicId) {
        ServiceRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        // Fetch mechanic email using the mechanic client
        String mechanicEmail = "unknown@example.com"; // fallback
        try {
            mechanicEmail = mechanicClient.getMechanicEmail(mechanicId).block();
        } catch (Exception e) {
            System.err.println("Failed to fetch mechanic email: " + e.getMessage());
        }

        request.setMechanicId(mechanicId);
        request.setMechanicEmail(mechanicEmail);
        request.setStatus(ServiceStatus.ASSIGNED);
        repository.save(request);

        // Set mechanic as BUSY when assigned to a job
        try {
            System.out.println("Marking mechanic " + mechanicId + " as BUSY after job assignment");
            mechanicClient.updateAvailability(mechanicId, "BUSY").block();
        } catch (Exception e) {
            System.err.println("Failed to update mechanic availability to BUSY: " + e.getMessage());
        }
    }

    // GET REQUESTS BY MECHANIC
    public List<ServiceRequestResponseDTO> getRequestsByMechanic(Long mechanicId) {
        return repository.findByMechanicId(mechanicId)
                .stream()
                .map(this::map)
                .toList();
    }

    // Logic to update status
    public ServiceRequestResponseDTO updateStatus(Long id, ServiceStatus status) {
        ServiceRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        request.setStatus(status);
        if (status == ServiceStatus.PAID) {
            request.setPaid(true);
        }
        
        ServiceRequest saved = repository.save(request);
        return map(saved);
    }

    // MAKE PAYMENT (CUSTOMER ONLY)
    public void makePayment(Long requestId, String userEmail) {
        ServiceRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        // Verify that the request belongs to the authenticated user
        if (!request.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("You can only make payment for your own service requests");
        }

        // Verify that the request is in PAYMENT_PENDING status
        if (request.getStatus() != ServiceStatus.PAYMENT_PENDING) {
            throw new RuntimeException("Payment can only be made for requests with PAYMENT_PENDING status");
        }

        // Set paid to true before updating status
        request.setPaid(true);
        repository.save(request);
        
        System.out.println("Payment completed for request " + requestId + ". Paid status set to true.");

        // Update status to PAID - this will trigger the completion flow
        updateStatus(requestId, ServiceStatus.PAID);
    }

    // GET PAYMENT DETAILS (CUSTOMER ONLY)
    public PaymentDetailsDTO getPaymentDetails(Long requestId, String userEmail) {
        ServiceRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        // Verify that the request belongs to the authenticated user
        if (!request.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("You can only view payment details for your own service requests");
        }

        // Get mechanic name
        String mechanicName = "Not Assigned";
        if (request.getMechanicId() != null) {
            try {
                mechanicName = mechanicClient.getMechanicName(request.getMechanicId()).block();
            } catch (Exception e) {
                mechanicName = "Unknown Mechanic";
            }
        }

        String message = "";
        if (request.getStatus() == ServiceStatus.PAYMENT_PENDING) {
            message = "Payment required to complete the service";
        } else if (request.getStatus() == ServiceStatus.PAID) {
            message = "Payment completed successfully";
        } else if (request.getStatus() == ServiceStatus.COMPLETED) {
            message = "Service completed and paid";
        } else {
            message = "Payment not available yet - Mechanic is still working on your request";
        }

        return PaymentDetailsDTO.builder()
                .requestId(request.getId())
                .problemType(request.getProblemType())
                .location(request.getLocation())
                .amount(request.getAmount())
                .mechanicName(mechanicName)
                .status(request.getStatus().toString())
                .message(message)
                .build();
    }

    private ServiceRequestResponseDTO map(ServiceRequest r) {
        String mechanicName = "Not Assigned";
        
        if (r.getMechanicId() != null) {
            try {
                System.out.println("Fetching mechanic name for mechanicId: " + r.getMechanicId());
                // Fetch mechanic name synchronously for simplicity
                mechanicName = mechanicClient.getMechanicName(r.getMechanicId()).block();
                System.out.println("Successfully fetched mechanic name: " + mechanicName);
            } catch (Exception e) {
                // Fallback if mechanic service is unavailable
                System.err.println("Failed to fetch mechanic name for ID " + r.getMechanicId() + ": " + e.getMessage());
                mechanicName = "Unknown Mechanic";
            }
        }

        return ServiceRequestResponseDTO.builder()
                .requestId(r.getId())
                .problemType(r.getProblemType())
                .location(r.getLocation())
                .amount(r.getAmount())
                .paid(r.isPaid())
                .status(r.getStatus())
                .userEmail(r.getUserEmail())
                .createdAt(r.getCreatedAt())
                .mechanicId(r.getMechanicId())
                .mechanicName(mechanicName)
                .build();
    }

    // Logic to get payment details
    public java.util.Map<String, Object> getPaymentStatus(Long requestId) {
        ServiceRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        boolean isPaid = request.getStatus() == ServiceStatus.PAID ||
                         request.getStatus() == ServiceStatus.COMPLETED ||
                         request.isPaid();
        return java.util.Map.of(
                "requestId", request.getId(),
                "status", request.getStatus().toString(),
                "canPay", request.getStatus() == ServiceStatus.PAYMENT_PENDING && !isPaid,
                "isPaid", isPaid,
                "paymentRequired", request.getStatus() == ServiceStatus.PAYMENT_PENDING,
                "message", request.getStatus().toString()
        );
    }

    private String getStatusMessage(ServiceStatus status) {
        switch (status) {
            case CREATED:
                return "Service request created";
            case PAYMENT_PENDING:
                return "Payment required - Mechanic has completed the job";
            case PAID:
                return "Payment completed successfully";
            case COMPLETED:
                return "Service completed and paid";
            case ASSIGNED:
                return "Mechanic assigned to your request";
            case IN_PROGRESS:
                return "Mechanic is working on your request";
            case CANCELLED:
                return "Service request cancelled";
            default:
                return "Unknown status";
        }
    }

    // UPDATE PAID STATUS (ADMIN/MECHANIC ONLY)
    public ServiceRequestResponseDTO updatePaidStatus(Long requestId, Boolean paid) {
        ServiceRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        request.setPaid(paid);
        repository.save(request);

        System.out.println("Updated paid status of request " + requestId + " to: " + paid);
        return map(request);
    }

    // GET PAID STATUS (CUSTOMER/ADMIN/MECHANIC)
    public Object getPaidStatus(Long requestId, String userEmail) {
        ServiceRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        // Verify ownership for customers, but allow admin/mechanic to access any
        if (userEmail != null && !userEmail.equals(request.getUserEmail())) {
            // This would need role checking in a real implementation
            // For now, we'll allow access since the endpoint already has @PreAuthorize
        }

        return java.util.Map.of(
            "requestId", request.getId(),
            "paid", request.isPaid(),
            "status", request.getStatus().toString(),
            "amount", request.getAmount(),
            "message", request.isPaid() ? "Payment completed" : "Payment pending"
        );
    }
}
