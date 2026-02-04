package com.blueharbor.hotel.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CompositePricingStrategy {

    private final List<RoomPricingStrategy> strategies;

    public CompositePricingStrategy(List<RoomPricingStrategy> strategies) {
        this.strategies = strategies;
    }

    public BigDecimal price(PricingContext context) {
        BigDecimal total = BigDecimal.ZERO;
        for (RoomPricingStrategy strategy : strategies) {
            total = strategy.apply(total, context);
        }
        return total;
    }
}
