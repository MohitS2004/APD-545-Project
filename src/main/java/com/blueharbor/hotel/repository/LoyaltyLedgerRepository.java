package com.blueharbor.hotel.repository;

import com.blueharbor.hotel.model.loyalty.LoyaltyLedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoyaltyLedgerRepository extends JpaRepository<LoyaltyLedgerEntry, UUID> {
}
