package com.blueharbor.hotel.dto;

import java.time.LocalDate;

public record OccupancyReportFilter(
    LocalDate from,
    LocalDate to,
    String granularity
) {
}
