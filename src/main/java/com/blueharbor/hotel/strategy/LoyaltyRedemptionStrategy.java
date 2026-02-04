package com.blueharbor.hotel.strategy;

import java.math.BigDecimal;

public interface LoyaltyRedemptionStrategy {
    BigDecimal redeem(BigDecimal subtotal, int availablePoints, double redemptionRate, double capPercent);
}
