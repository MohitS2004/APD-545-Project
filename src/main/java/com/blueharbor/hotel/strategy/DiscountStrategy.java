package com.blueharbor.hotel.strategy;

import com.blueharbor.hotel.model.AdminRole;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal apply(BigDecimal subtotal, AdminRole role, double requestedPercent);
}
