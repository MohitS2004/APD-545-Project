package com.blueharbor.hotel.service;

import com.blueharbor.hotel.dto.FeedbackSubmission;
import com.blueharbor.hotel.model.Reservation;
import com.blueharbor.hotel.model.ReservationStatus;
import com.blueharbor.hotel.model.feedback.Feedback;
import com.blueharbor.hotel.model.feedback.FeedbackSentiment;
import com.blueharbor.hotel.repository.FeedbackRepository;
import com.blueharbor.hotel.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class FeedbackService {

    private final ReservationRepository reservationRepository;
    private final FeedbackRepository feedbackRepository;

    public FeedbackService(ReservationRepository reservationRepository, FeedbackRepository feedbackRepository) {
        this.reservationRepository = reservationRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @Transactional
    public Feedback submit(FeedbackSubmission submission) {
        Reservation reservation = reservationRepository.findByConfirmationCode(submission.confirmationCode())
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        if (reservation.getStatus() != ReservationStatus.CHECKED_OUT) {
            throw new IllegalStateException("Feedback allowed only after checkout");
        }
        Feedback feedback = reservation.getFeedback();
        if (feedback == null) {
            feedback = new Feedback();
            feedback.setReservation(reservation);
        }
        feedback.setRating(submission.rating());
        feedback.setComment(submission.comment());
        feedback.setSentiment(sentimentFor(submission.rating()));
        feedback.setSubmittedAt(LocalDateTime.now());
        reservation.setFeedback(feedback);
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> listAll() {
        return feedbackRepository.findAll()
            .stream()
            .sorted(Comparator.comparing(Feedback::getSubmittedAt).reversed())
            .toList();
    }

    private FeedbackSentiment sentimentFor(int rating) {
        if (rating >= 4) {
            return FeedbackSentiment.POSITIVE;
        }
        if (rating == 3) {
            return FeedbackSentiment.NEUTRAL;
        }
        return FeedbackSentiment.NEGATIVE;
    }
}
