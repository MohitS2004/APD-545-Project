package com.blueharbor.hotel.service;

import com.blueharbor.hotel.config.ConfigRegistry;
import com.blueharbor.hotel.config.PricingProperties;
import com.blueharbor.hotel.dto.ReservationEstimate;
import com.blueharbor.hotel.dto.ReservationRequest;
import com.blueharbor.hotel.model.RoomType;
import com.blueharbor.hotel.model.addon.AddOnCode;
import com.blueharbor.hotel.strategy.CompositePricingStrategy;
import com.blueharbor.hotel.strategy.PricingContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PricingService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.08");

    private final ConfigRegistry registry;
    private final CompositePricingStrategy pricingStrategy;

    public PricingService(ConfigRegistry registry, CompositePricingStrategy pricingStrategy) {
        this.registry = registry;
        this.pricingStrategy = pricingStrategy;
    }

    public ReservationEstimate estimate(ReservationRequest request) {
        int nights = (int) ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());
        BigDecimal roomSubtotal = request.roomCounts().entrySet().stream()
            .map(entry -> priceRoom(entry.getKey(), entry.getValue(), request))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> addOnBreakdown = new HashMap<>();
        List<AddOnCode> addOns = request.addOns() == null ? List.of() : request.addOns();
        BigDecimal addOnTotal = addOns.stream()
            .map(addOn -> priceAddOn(addOn, nights, addOnBreakdown))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal subtotal = roomSubtotal.add(addOnTotal);
        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);
        return new ReservationEstimate(
            nights,
            subtotal.setScale(2, RoundingMode.HALF_UP),
            tax,
            total.setScale(2, RoundingMode.HALF_UP),
            addOnBreakdown,
            Map.of()
        );
    }

    private BigDecimal priceRoom(RoomType roomType, Integer quantity, ReservationRequest request) {
        double baseRate = registry.pricing().getBaseRates().get(roomType.name()).doubleValue();
        PricingContext context = new PricingContext(roomType, request.checkIn(), request.checkOut(), quantity, baseRate);
        return pricingStrategy.price(context);
    }

    private BigDecimal priceAddOn(AddOnCode code, int nights, Map<String, BigDecimal> breakdown) {
        PricingProperties.AddOnDefinition definition = registry.addOns().get(code.name());
        if (definition == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal cost = definition.getPrice();
        if (definition.isPerNight()) {
            cost = cost.multiply(BigDecimal.valueOf(nights));
        }
        breakdown.put(code.name(), cost);
        return cost;
    }
}
