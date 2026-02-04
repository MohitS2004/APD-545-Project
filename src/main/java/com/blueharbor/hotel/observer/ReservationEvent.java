package com.blueharbor.hotel.observer;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationEvent(
    ReservationEventType type,
    UUID reservationId,
    String details,
    LocalDateTime at
) {
    public static ReservationEvent of(ReservationEventType type, UUID reservationId, String details) {
        return new ReservationEvent(type, reservationId, details, LocalDateTime.now());
    }
}
