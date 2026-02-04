package com.blueharbor.hotel.model.report;

import java.time.LocalDateTime;

public record ActivityRow(
    LocalDateTime timestamp,
    String actor,
    String action,
    String entityType,
    String entityIdentifier,
    String message
) {
}
