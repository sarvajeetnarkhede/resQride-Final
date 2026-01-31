package com.ride.servicerequest.service;

import com.ride.servicerequest.dto.PaymentReceiptDTO;
import com.ride.servicerequest.entity.ServiceRequest;
import com.ride.servicerequest.repository.ServiceRequestRepository;
import com.ride.servicerequest.client.MechanicClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final MechanicClient mechanicClient;

    public PaymentReceiptDTO generateReceipt(Long requestId, String userEmail) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        // Verify ownership
        if (!request.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("You can only generate receipts for your own service requests");
        }

        // Verify payment is completed
        if (request.getStatus() != com.ride.servicerequest.dto.ServiceStatus.COMPLETED) {
            throw new RuntimeException("Receipt can only be generated for completed and paid services");
        }

        // Get mechanic details
        String mechanicName = "Not Assigned";
        String mechanicSkillType = "Unknown";
        if (request.getMechanicId() != null) {
            try {
                mechanicName = mechanicClient.getMechanicName(request.getMechanicId()).block();
                mechanicSkillType = "Professional Service"; // Default skill type
            } catch (Exception e) {
                mechanicName = "Unknown Mechanic";
                mechanicSkillType = "Unknown";
            }
        }

        // Calculate amounts (you could add tax logic here)
        Double taxAmount = request.getAmount() * 0.18; // 18% tax for example
        Double totalAmount = request.getAmount() + taxAmount;

        return PaymentReceiptDTO.builder()
                .receiptId("RCP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .requestId(request.getId())
                .transactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .paymentDate(LocalDateTime.now())
                .amount(request.getAmount())
                .paymentMethod("ONLINE")
                .paymentStatus("COMPLETED")
                .customerEmail(request.getUserEmail())
                .customerName(extractCustomerName(request.getUserEmail()))
                .problemType(request.getProblemType())
                .location(request.getLocation())
                .serviceDate(request.getCreatedAt())
                .mechanicId(request.getMechanicId())
                .mechanicName(mechanicName)
                .mechanicSkillType(mechanicSkillType)
                .currency("INR")
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .description("Payment for " + request.getProblemType() + " service at " + request.getLocation())
                .generatedAt(LocalDateTime.now())
                .verificationCode(generateVerificationCode(requestId))
                .build();
    }

    private String extractCustomerName(String email) {
        // Simple extraction - you could enhance this to fetch from user service
        return email.split("@")[0];
    }

    private String generateVerificationCode(Long requestId) {
        return "VER-" + requestId + "-" + System.currentTimeMillis() % 10000;
    }
}
