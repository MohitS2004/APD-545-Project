package com.blueharbor.hotel.service;

import com.blueharbor.hotel.dto.WaitlistRequest;
import com.blueharbor.hotel.logging.ActivityLogger;
import com.blueharbor.hotel.model.RoomType;
import com.blueharbor.hotel.model.activity.ActivityAction;
import com.blueharbor.hotel.model.waitlist.WaitlistEntry;
import com.blueharbor.hotel.model.waitlist.WaitlistStatus;
import com.blueharbor.hotel.observer.ReservationEvent;
import com.blueharbor.hotel.repository.WaitlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class WaitlistService {

    private final WaitlistRepository waitlistRepository;
    private final ActivityLogger activityLogger;

    public WaitlistService(WaitlistRepository waitlistRepository, ActivityLogger activityLogger) {
        this.waitlistRepository = waitlistRepository;
        this.activityLogger = activityLogger;
    }

    public WaitlistEntry addToWaitlist(WaitlistRequest request) {
        WaitlistEntry entry = new WaitlistEntry();
        entry.setGuestName(request.guestName());
        entry.setPhone(request.phone());
        entry.setEmail(request.email());
        entry.setDesiredRoomType(request.roomType());
        entry.setStartDate(request.startDate());
        entry.setEndDate(request.endDate());
        entry.setNotes(request.notes());
        entry.setStatus(WaitlistStatus.PENDING);
        WaitlistEntry saved = waitlistRepository.save(entry);
        activityLogger.log(ActivityAction.WAITLIST_CREATED, request.email(), "Waitlist",
            saved.getId().toString(), "Guest added to waitlist");
        return saved;
    }

    public List<WaitlistEntry> list(WaitlistStatus status) {
        if (status == null) {
            return waitlistRepository.findAll();
        }
        return waitlistRepository.findByStatus(status);
    }

    @Transactional
    public void handleAvailabilityChange(ReservationEvent event) {
        // Basic strategy: mark all pending entries for the room type and date range as NOTIFIED.
        List<WaitlistEntry> pending = waitlistRepository.findByStatus(WaitlistStatus.PENDING);
        for (WaitlistEntry entry : pending) {
            entry.setStatus(WaitlistStatus.NOTIFIED);
            waitlistRepository.save(entry);
            activityLogger.log(ActivityAction.WAITLIST_CONVERTED, "system", "Waitlist",
                entry.getId().toString(), "Availability changed, notify admin");
        }
    }

    public void markConverted(UUID entryId) {
        waitlistRepository.findById(entryId).ifPresent(entry -> {
            entry.setStatus(WaitlistStatus.CONVERTED);
            waitlistRepository.save(entry);
        });
    }
}
