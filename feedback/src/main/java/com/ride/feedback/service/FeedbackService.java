package com.ride.feedback.service;

import lombok.RequiredArgsConstructor;
import com.ride.feedback.dto.*;
import com.ride.feedback.model.Feedback;
import com.ride.feedback.repository.FeedbackRepository;
import com.ride.feedback.client.MechanicClient;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository repository;
    private final MechanicClient mechanicClient;

    public FeedbackResponseDTO create(String email, FeedbackCreateDTO dto) {

        // Prevent duplicate feedback per request
        if (repository.existsByRequestIdAndUserEmail(dto.getRequestId(), email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Feedback already submitted for this request");
        }

        Feedback feedback = Feedback.builder()
                .requestId(dto.getRequestId())
                .mechanicId(dto.getMechanicId())
                .userEmail(email)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        FeedbackResponseDTO response = map(repository.save(feedback));
        
        // Update mechanic's average rating
        updateMechanicRating(dto.getMechanicId());
        
        return response;
    }

    public FeedbackResponseDTO update(String email, FeedbackCreateDTO dto) {
        Feedback feedback = repository.findByRequestIdAndUserEmail(dto.getRequestId(), email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No feedback found for this request"));

        feedback.setRating(dto.getRating());
        feedback.setComment(dto.getComment());
        feedback.setCreatedAt(LocalDateTime.now()); // Update timestamp

        FeedbackResponseDTO response = map(repository.save(feedback));
        
        // Update mechanic's average rating
        updateMechanicRating(dto.getMechanicId());
        
        return response;
    }

    public List<FeedbackResponseDTO> byMechanic(Long mechanicId) {
        return repository.findByMechanicId(mechanicId)
                .stream()
                .map(this::map)
                .toList();
    }

    public List<FeedbackResponseDTO> byRequest(Long requestId) {
        return repository.findByRequestId(requestId)
                .stream()
                .map(this::map)
                .toList();
    }

    public Double getAverageRatingForMechanic(Long mechanicId) {
        List<Feedback> feedbacks = repository.findByMechanicId(mechanicId);
        
        if (feedbacks.isEmpty()) {
            return 0.0; // No feedback yet
        }
        
        double average = feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
        
        // Round to 2 decimal places
        return Math.round(average * 100.0) / 100.0;
    }

    private void updateMechanicRating(Long mechanicId) {
        try {
            Double newAverageRating = getAverageRatingForMechanic(mechanicId);
            mechanicClient.updateMechanicRating(mechanicId, newAverageRating).block();
        } catch (Exception e) {
            System.err.println("Failed to update mechanic rating for ID " + mechanicId + ": " + e.getMessage());
        }
    }

    private FeedbackResponseDTO map(Feedback f) {
        return FeedbackResponseDTO.builder()
                .id(f.getId())
                .requestId(f.getRequestId())
                .mechanicId(f.getMechanicId())
                .userEmail(f.getUserEmail())
                .rating(f.getRating())
                .comment(f.getComment())
                .createdAt(f.getCreatedAt())
                .build();
    }
}
