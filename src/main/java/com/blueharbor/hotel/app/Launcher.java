package com.blueharbor.hotel.app;

/**
 * Plain Java launcher used by Maven/IntelliJ to start the JavaFX application.
 */
public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        javafx.application.Application.launch(HotelReservationApplication.class, args);
    }
}
