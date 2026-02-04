package com.blueharbor.hotel.observer;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationEventPublisher {

    private final List<ReservationEventListener> listeners;

    public ReservationEventPublisher(List<ReservationEventListener> listeners) {
        this.listeners = listeners;
    }

    public void publish(ReservationEvent event) {
        listeners.forEach(listener -> listener.onEvent(event));
    }
}
