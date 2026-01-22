package com.ride.feedback.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeedbackResponseDTO {
    private String id;
    private Long requestId;
    private Long mechanicId;
    private String userEmail;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
