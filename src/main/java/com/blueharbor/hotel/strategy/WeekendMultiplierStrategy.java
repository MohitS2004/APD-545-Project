package com.blueharbor.hotel.strategy;

import com.blueharbor.hotel.config.ConfigRegistry;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
public class WeekendMultiplierStrategy implements RoomPricingStrategy {

    private final ConfigRegistry configRegistry;

    public WeekendMultiplierStrategy(ConfigRegistry configRegistry) {
        this.configRegistry = configRegistry;
    }

    @Override
    public BigDecimal apply(BigDecimal currentTotal, PricingContext context) {
        double multiplier = configRegistry.pricing().getWeekendMultiplier();
        if (multiplier <= 1.0) {
            return currentTotal;
        }
        BigDecimal delta = BigDecimal.ZERO;
        BigDecimal nightlyBase = BigDecimal.valueOf(context.baseRate())
            .multiply(BigDecimal.valueOf(context.quantity()));
        LocalDate cursor = context.checkIn();
        for (int i = 0; i < context.nights(); i++) {
            DayOfWeek day = cursor.plusDays(i).getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                BigDecimal weekendIncrement = nightlyBase.multiply(BigDecimal.valueOf(multiplier - 1.0));
                delta = delta.add(weekendIncrement);
            }
        }
        return currentTotal.add(delta);
    }
}
