package com.blueharbor.hotel.strategy;

import java.math.BigDecimal;

public interface RoomPricingStrategy {
    BigDecimal apply(BigDecimal currentTotal, PricingContext context);
}
