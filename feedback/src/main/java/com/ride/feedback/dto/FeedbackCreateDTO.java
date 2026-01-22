package com.ride.feedback.dto;

import lombok.Data;

@Data
public class FeedbackCreateDTO {
    private Long requestId;
    private Long mechanicId;
    private Integer rating;
    private String comment;
}
