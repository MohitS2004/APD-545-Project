package com.blueharbor.hotel.observer;

import com.blueharbor.hotel.logging.ActivityLogger;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogListener implements ReservationEventListener {

    private final ActivityLogger logger;

    public ActivityLogListener(ActivityLogger logger) {
        this.logger = logger;
    }

    @Override
    public void onEvent(ReservationEvent event) {
        logger.logEvent(event);
    }
}
