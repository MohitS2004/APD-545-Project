package com.blueharbor.hotel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.discounts")
public class DiscountPolicyProperties {

    private double adminCap;
    private double managerCap;

    public double getAdminCap() {
        return adminCap;
    }

    public void setAdminCap(double adminCap) {
        this.adminCap = adminCap;
    }

    public double getManagerCap() {
        return managerCap;
    }

    public void setManagerCap(double managerCap) {
        this.managerCap = managerCap;
    }
}
