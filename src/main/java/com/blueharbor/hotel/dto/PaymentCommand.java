package com.blueharbor.hotel.dto;

import com.blueharbor.hotel.model.billing.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCommand(
    UUID reservationId,
    PaymentMethod method,
    BigDecimal amount,
    boolean refund,
    String actorEmail,
    String reference
) {
}
