package com.blueharbor.hotel.repository;

import com.blueharbor.hotel.model.RoomType;
import com.blueharbor.hotel.model.waitlist.WaitlistEntry;
import com.blueharbor.hotel.model.waitlist.WaitlistStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WaitlistRepository extends JpaRepository<WaitlistEntry, UUID> {
    List<WaitlistEntry> findByStatus(WaitlistStatus status);

    List<WaitlistEntry> findByDesiredRoomTypeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        RoomType roomType,
        LocalDate start,
        LocalDate end
    );
}
