package com.blueharbor.hotel.strategy;

import com.blueharbor.hotel.config.ConfigRegistry;
import com.blueharbor.hotel.config.PricingProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;

@Component
public class SeasonalMultiplierStrategy implements RoomPricingStrategy {

    private final List<PricingProperties.SeasonalMultiplier> seasonalMultipliers;

    public SeasonalMultiplierStrategy(ConfigRegistry registry) {
        this.seasonalMultipliers = registry.pricing().getSeasonal();
    }

    @Override
    public BigDecimal apply(BigDecimal currentTotal, PricingContext context) {
        if (seasonalMultipliers == null || seasonalMultipliers.isEmpty()) {
            return currentTotal;
        }

        BigDecimal delta = BigDecimal.ZERO;
        BigDecimal nightlyBase = BigDecimal.valueOf(context.baseRate())
            .multiply(BigDecimal.valueOf(context.quantity()));
        for (int i = 0; i < context.nights(); i++) {
            LocalDate date = context.checkIn().plusDays(i);
            double multiplier = multiplierFor(date);
            if (multiplier > 1.0) {
                delta = delta.add(nightlyBase.multiply(BigDecimal.valueOf(multiplier - 1.0)));
            }
        }
        return currentTotal.add(delta);
    }

    private double multiplierFor(LocalDate date) {
        MonthDay day = MonthDay.from(date);
        return seasonalMultipliers.stream()
            .filter(season -> occursOn(day, season))
            .mapToDouble(PricingProperties.SeasonalMultiplier::getMultiplier)
            .max()
            .orElse(1.0);
    }

    private boolean occursOn(MonthDay day, PricingProperties.SeasonalMultiplier definition) {
        MonthDay start = MonthDay.parse("--" + definition.getStart());
        MonthDay end = MonthDay.parse("--" + definition.getEnd());
        if (start.isBefore(end) || start.equals(end)) {
            return (day.isAfter(start) || day.equals(start)) && (day.isBefore(end) || day.equals(end));
        }
        // wrap-around season (e.g., Dec 15 - Jan 05)
        return day.isAfter(start) || day.isBefore(end) || day.equals(start) || day.equals(end);
    }
}
