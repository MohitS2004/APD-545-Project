package com.blueharbor.hotel.observer;

import com.blueharbor.hotel.service.WaitlistService;
import org.springframework.stereotype.Component;

@Component
public class WaitlistAvailabilityListener implements ReservationEventListener {

    private final WaitlistService waitlistService;

    public WaitlistAvailabilityListener(WaitlistService waitlistService) {
        this.waitlistService = waitlistService;
    }

    @Override
    public void onEvent(ReservationEvent event) {
        if (event.type() == ReservationEventType.CHECKED_OUT
            || event.type() == ReservationEventType.CANCELLED
            || event.type() == ReservationEventType.AVAILABILITY_CHANGED) {
            waitlistService.handleAvailabilityChange(event);
        }
    }
}
