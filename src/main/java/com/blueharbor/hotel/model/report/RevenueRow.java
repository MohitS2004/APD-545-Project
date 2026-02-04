package com.blueharbor.hotel.model.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RevenueRow(
    String periodLabel,
    LocalDate startDate,
    LocalDate endDate,
    long reservations,
    BigDecimal subtotal,
    BigDecimal tax,
    BigDecimal discounts,
    BigDecimal total
) {
}
