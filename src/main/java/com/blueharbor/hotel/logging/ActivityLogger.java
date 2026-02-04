package com.blueharbor.hotel.logging;

import com.blueharbor.hotel.model.activity.ActivityAction;
import com.blueharbor.hotel.model.activity.ActivityLog;
import com.blueharbor.hotel.observer.ReservationEvent;
import com.blueharbor.hotel.observer.ReservationEventType;
import com.blueharbor.hotel.repository.ActivityLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogger {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityLogger.class);

    private final ActivityLogRepository repository;

    public ActivityLogger(ActivityLogRepository repository) {
        this.repository = repository;
    }

    public void log(ActivityAction action, String actor, String entityType, String identifier, String message) {
        ActivityLog entry = new ActivityLog();
        entry.setAction(action);
        entry.setActor(actor);
        entry.setEntityType(entityType);
        entry.setEntityIdentifier(identifier);
        entry.setMessage(message);
        repository.save(entry);
        LOG.info("[{}] {} - {}", action, identifier, message);
    }

    public void logEvent(ReservationEvent event) {
        ActivityAction action = switch (event.type()) {
            case CREATED -> ActivityAction.RESERVATION_CREATED;
            case UPDATED -> ActivityAction.RESERVATION_UPDATED;
            case CANCELLED -> ActivityAction.RESERVATION_CANCELLED;
            case CHECKED_OUT -> ActivityAction.CHECKOUT_COMPLETED;
            case PAYMENT_CAPTURED -> ActivityAction.PAYMENT_CAPTURED;
            case AVAILABILITY_CHANGED -> ActivityAction.WAITLIST_CONVERTED;
        };
        log(action, "system", "Reservation", event.reservationId().toString(), event.details());
    }
}
