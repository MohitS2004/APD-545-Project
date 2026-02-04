package com.blueharbor.hotel.dto;

import java.util.UUID;

public record FeedbackSubmission(
    String confirmationCode,
    int rating,
    String comment
) {
}
