package com.blueharbor.hotel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.occupancy")
public class OccupancyPolicyProperties {

    private int single;
    private int doubleRoom;
    private int deluxe;
    private int penthouse;

    public int getSingle() {
        return single;
    }

    public void setSingle(int single) {
        this.single = single;
    }

    public int getDoubleRoom() {
        return doubleRoom;
    }

    public void setDoubleRoom(int doubleRoom) {
        this.doubleRoom = doubleRoom;
    }

    public int getDeluxe() {
        return deluxe;
    }

    public void setDeluxe(int deluxe) {
        this.deluxe = deluxe;
    }

    public int getPenthouse() {
        return penthouse;
    }

    public void setPenthouse(int penthouse) {
        this.penthouse = penthouse;
    }
}
