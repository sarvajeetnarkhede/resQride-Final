package com.ride.feedback.service;

import lombok.RequiredArgsConstructor;
import com.ride.feedback.dto.*;
import com.ride.feedback.model.Feedback;
import com.ride.feedback.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository repository;

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

        return map(repository.save(feedback));
    }

    public FeedbackResponseDTO update(String email, FeedbackCreateDTO dto) {
        Feedback feedback = repository.findByRequestIdAndUserEmail(dto.getRequestId(), email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No feedback found for this request"));

        feedback.setRating(dto.getRating());
        feedback.setComment(dto.getComment());
        feedback.setCreatedAt(LocalDateTime.now()); // Update timestamp

        return map(repository.save(feedback));
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
