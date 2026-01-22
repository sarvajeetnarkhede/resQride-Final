package com.ride.feedback.repository;

import com.ride.feedback.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FeedbackRepository
        extends MongoRepository<Feedback, String> {

    List<Feedback> findByMechanicId(Long mechanicId);

    List<Feedback> findByRequestId(Long requestId);

    boolean existsByRequestIdAndUserEmail(Long requestId, String userEmail);
}
