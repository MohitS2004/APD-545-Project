package com.blueharbor.hotel.model.addon;

import com.blueharbor.hotel.model.RoomType;

import java.math.BigDecimal;

public class RoomComponent implements PricedComponent {

    private final RoomType type;
    private final BigDecimal nightlyRate;
    private final int nights;
    private final int quantity;

    public RoomComponent(RoomType type, BigDecimal nightlyRate, int nights, int quantity) {
        this.type = type;
        this.nightlyRate = nightlyRate;
        this.nights = nights;
        this.quantity = quantity;
    }

    @Override
    public BigDecimal price() {
        return nightlyRate.multiply(BigDecimal.valueOf(nights * quantity));
    }

    @Override
    public String description() {
        return quantity + " x " + type + " (" + nights + " nights)";
    }
}
