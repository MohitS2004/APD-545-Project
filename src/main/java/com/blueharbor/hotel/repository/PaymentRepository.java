package com.blueharbor.hotel.repository;

import com.blueharbor.hotel.model.billing.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByProcessedAtBetween(LocalDateTime from, LocalDateTime to);
}
