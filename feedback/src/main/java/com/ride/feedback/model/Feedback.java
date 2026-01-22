package com.ride.feedback.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "feedbacks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    private String id;

    private Long requestId;
    private Long mechanicId;
    private String userEmail;

    private Integer rating;   // 1–5
    private String comment;

    private LocalDateTime createdAt;
}
