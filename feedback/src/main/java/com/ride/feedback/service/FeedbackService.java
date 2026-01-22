package com.ride.feedback.service;

import lombok.RequiredArgsConstructor;
import com.ride.feedback.dto.*;
import com.ride.feedback.model.Feedback;
import com.ride.feedback.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository repository;

    public FeedbackResponseDTO create(String email, FeedbackCreateDTO dto) {

        // Prevent duplicate feedback per request
        if (repository.existsByRequestIdAndUserEmail(dto.getRequestId(), email)) {
            throw new RuntimeException("Feedback already submitted for this request");
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
