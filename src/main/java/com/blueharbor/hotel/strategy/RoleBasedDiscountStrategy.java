package com.blueharbor.hotel.strategy;

import com.blueharbor.hotel.config.ConfigRegistry;
import com.blueharbor.hotel.model.AdminRole;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class RoleBasedDiscountStrategy implements DiscountStrategy {

    private final ConfigRegistry registry;

    public RoleBasedDiscountStrategy(ConfigRegistry registry) {
        this.registry = registry;
    }

    @Override
    public BigDecimal apply(BigDecimal subtotal, AdminRole role, double requestedPercent) {
        double cap = switch (role) {
            case ADMIN -> registry.discounts().getAdminCap();
            case MANAGER -> registry.discounts().getManagerCap();
        };
        double effectivePercent = Math.min(requestedPercent, cap);
        return subtotal.multiply(BigDecimal.valueOf(effectivePercent))
            .setScale(2, RoundingMode.HALF_UP);
    }
}
