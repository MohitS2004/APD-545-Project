package com.blueharbor.hotel.repository;

import com.blueharbor.hotel.model.feedback.Feedback;
import com.blueharbor.hotel.model.feedback.FeedbackSentiment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    List<Feedback> findByRatingGreaterThanEqual(int rating);

    List<Feedback> findBySentimentAndSubmittedAtBetween(
        FeedbackSentiment sentiment,
        LocalDateTime from,
        LocalDateTime to
    );
}
