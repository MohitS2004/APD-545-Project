package com.blueharbor.hotel.repository;

import com.blueharbor.hotel.model.activity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
    List<ActivityLog> findByTimestampBetween(LocalDateTime from, LocalDateTime to);
}
