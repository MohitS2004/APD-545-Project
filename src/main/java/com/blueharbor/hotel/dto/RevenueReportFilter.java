package com.blueharbor.hotel.dto;

import java.time.LocalDate;

public record RevenueReportFilter(
    LocalDate from,
    LocalDate to,
    String granularity // day, week, month
) {
}
