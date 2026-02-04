package com.blueharbor.hotel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.loyalty")
public class LoyaltyProperties {

    private double earningRate;
    private double redemptionRate;
    private double redemptionCapPercent;
    private int enrollmentBonus;

    public double getEarningRate() {
        return earningRate;
    }

    public void setEarningRate(double earningRate) {
        this.earningRate = earningRate;
    }

    public double getRedemptionRate() {
        return redemptionRate;
    }

    public void setRedemptionRate(double redemptionRate) {
        this.redemptionRate = redemptionRate;
    }

    public double getRedemptionCapPercent() {
        return redemptionCapPercent;
    }

    public void setRedemptionCapPercent(double redemptionCapPercent) {
        this.redemptionCapPercent = redemptionCapPercent;
    }

    public int getEnrollmentBonus() {
        return enrollmentBonus;
    }

    public void setEnrollmentBonus(int enrollmentBonus) {
        this.enrollmentBonus = enrollmentBonus;
    }
}
