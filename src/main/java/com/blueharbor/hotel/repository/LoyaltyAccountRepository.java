package com.blueharbor.hotel.repository;

import com.blueharbor.hotel.model.loyalty.LoyaltyAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, UUID> {
    Optional<LoyaltyAccount> findByMembershipNumber(String membershipNumber);
}
