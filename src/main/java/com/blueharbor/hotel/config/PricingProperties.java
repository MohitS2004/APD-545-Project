package com.blueharbor.hotel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "app.pricing")
public class PricingProperties {

    private double weekdayMultiplier = 1.0;
    private double weekendMultiplier = 1.2;
    private List<SeasonalMultiplier> seasonal;
    private Map<String, BigDecimal> baseRates;
    private Map<String, AddOnDefinition> addOns;

    public double getWeekdayMultiplier() {
        return weekdayMultiplier;
    }

    public void setWeekdayMultiplier(double weekdayMultiplier) {
        this.weekdayMultiplier = weekdayMultiplier;
    }

    public double getWeekendMultiplier() {
        return weekendMultiplier;
    }

    public void setWeekendMultiplier(double weekendMultiplier) {
        this.weekendMultiplier = weekendMultiplier;
    }

    public List<SeasonalMultiplier> getSeasonal() {
        return seasonal;
    }

    public void setSeasonal(List<SeasonalMultiplier> seasonal) {
        this.seasonal = seasonal;
    }

    public Map<String, BigDecimal> getBaseRates() {
        return baseRates;
    }

    public void setBaseRates(Map<String, BigDecimal> baseRates) {
        this.baseRates = baseRates;
    }

    public Map<String, AddOnDefinition> getAddOns() {
        return addOns;
    }

    public void setAddOns(Map<String, AddOnDefinition> addOns) {
        this.addOns = addOns;
    }

    public static class SeasonalMultiplier {
        private String name;
        private double multiplier;
        private String start;
        private String end;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getMultiplier() {
            return multiplier;
        }

        public void setMultiplier(double multiplier) {
            this.multiplier = multiplier;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }

    public static class AddOnDefinition {
        private BigDecimal price;
        private boolean perNight;

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public boolean isPerNight() {
            return perNight;
        }

        public void setPerNight(boolean perNight) {
            this.perNight = perNight;
        }
    }
}
