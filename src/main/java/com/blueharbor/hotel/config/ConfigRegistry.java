package com.blueharbor.hotel.config;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConfigRegistry {

    private final HotelSettingsProperties hotel;
    private final OccupancyPolicyProperties occupancy;
    private final PricingProperties pricing;
    private final LoyaltyProperties loyalty;
    private final DiscountPolicyProperties discountPolicy;
    private final ReportingProperties reporting;

    public ConfigRegistry(
        HotelSettingsProperties hotel,
        OccupancyPolicyProperties occupancy,
        PricingProperties pricing,
        LoyaltyProperties loyalty,
        DiscountPolicyProperties discountPolicy,
        ReportingProperties reporting
    ) {
        this.hotel = hotel;
        this.occupancy = occupancy;
        this.pricing = pricing;
        this.loyalty = loyalty;
        this.discountPolicy = discountPolicy;
        this.reporting = reporting;
    }

    public HotelSettingsProperties hotel() {
        return hotel;
    }

    public OccupancyPolicyProperties occupancy() {
        return occupancy;
    }

    public PricingProperties pricing() {
        return pricing;
    }

    public LoyaltyProperties loyalty() {
        return loyalty;
    }

    public DiscountPolicyProperties discounts() {
        return discountPolicy;
    }

    public ReportingProperties reporting() {
        return reporting;
    }

    public int occupancyLimit(String roomType) {
        return switch (roomType.toUpperCase()) {
            case "SINGLE" -> occupancy.getSingle();
            case "DOUBLE" -> occupancy.getDoubleRoom();
            case "DELUXE" -> occupancy.getDeluxe();
            case "PENTHOUSE" -> occupancy.getPenthouse();
            default -> throw new IllegalArgumentException("Unknown room type " + roomType);
        };
    }

    public Map<String, PricingProperties.AddOnDefinition> addOns() {
        return pricing.getAddOns();
    }
}
