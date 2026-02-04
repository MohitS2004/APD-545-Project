package com.blueharbor.hotel.dto;

import java.math.BigDecimal;
import java.util.Map;

public record ReservationEstimate(
    int nights,
    BigDecimal subtotal,
    BigDecimal tax,
    BigDecimal total,
    Map<String, BigDecimal> addOnBreakdown,
    Map<String, BigDecimal> loyaltyBreakdown
) {
}
