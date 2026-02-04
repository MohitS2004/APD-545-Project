package com.blueharbor.hotel.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CappedLoyaltyRedemptionStrategy implements LoyaltyRedemptionStrategy {

    @Override
    public BigDecimal redeem(BigDecimal subtotal, int availablePoints, double redemptionRate, double capPercent) {
        BigDecimal maxDiscount = subtotal.multiply(BigDecimal.valueOf(capPercent))
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal pointsValue = BigDecimal.valueOf(availablePoints)
            .multiply(BigDecimal.valueOf(redemptionRate))
            .setScale(2, RoundingMode.HALF_UP);
        return pointsValue.min(maxDiscount);
    }
}
