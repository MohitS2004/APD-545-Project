package com.blueharbor.hotel.observer;

public interface ReservationEventListener {
    void onEvent(ReservationEvent event);
}
