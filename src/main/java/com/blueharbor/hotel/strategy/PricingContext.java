package com.blueharbor.hotel.strategy;

import com.blueharbor.hotel.model.RoomType;

import java.time.LocalDate;

public class PricingContext {

    private final RoomType roomType;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final int quantity;
    private final double baseRate;

    public PricingContext(RoomType roomType, LocalDate checkIn, LocalDate checkOut, int quantity, double baseRate) {
        this.roomType = roomType;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.quantity = quantity;
        this.baseRate = baseRate;
    }

    public RoomType roomType() {
        return roomType;
    }

    public LocalDate checkIn() {
        return checkIn;
    }

    public LocalDate checkOut() {
        return checkOut;
    }

    public int quantity() {
        return quantity;
    }

    public int nights() {
        return (int) (checkOut.toEpochDay() - checkIn.toEpochDay());
    }

    public double baseRate() {
        return baseRate;
    }
}
