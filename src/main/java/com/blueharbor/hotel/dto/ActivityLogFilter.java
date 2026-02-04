package com.blueharbor.hotel.dto;

import java.time.LocalDateTime;

public record ActivityLogFilter(
    LocalDateTime from,
    LocalDateTime to
) {
}
