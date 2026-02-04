package com.blueharbor.hotel.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BaseRateStrategy implements RoomPricingStrategy {

    @Override
    public BigDecimal apply(BigDecimal currentTotal, PricingContext context) {
        BigDecimal base = BigDecimal.valueOf(context.baseRate())
            .multiply(BigDecimal.valueOf(context.nights()))
            .multiply(BigDecimal.valueOf(context.quantity()));
        return currentTotal.add(base);
    }
}
