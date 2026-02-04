package com.blueharbor.hotel.service;

import com.blueharbor.hotel.config.ConfigRegistry;
import com.blueharbor.hotel.model.Guest;
import com.blueharbor.hotel.model.loyalty.LoyaltyAccount;
import com.blueharbor.hotel.model.loyalty.LoyaltyLedgerEntry;
import com.blueharbor.hotel.model.loyalty.LoyaltyTransactionType;
import com.blueharbor.hotel.repository.LoyaltyAccountRepository;
import com.blueharbor.hotel.repository.LoyaltyLedgerRepository;
import com.blueharbor.hotel.strategy.LoyaltyRedemptionStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoyaltyService {

    private final LoyaltyAccountRepository accountRepository;
    private final LoyaltyLedgerRepository ledgerRepository;
    private final ConfigRegistry registry;
    private final LoyaltyRedemptionStrategy redemptionStrategy;

    public LoyaltyService(
        LoyaltyAccountRepository accountRepository,
        LoyaltyLedgerRepository ledgerRepository,
        ConfigRegistry registry,
        LoyaltyRedemptionStrategy redemptionStrategy
    ) {
        this.accountRepository = accountRepository;
        this.ledgerRepository = ledgerRepository;
        this.registry = registry;
        this.redemptionStrategy = redemptionStrategy;
    }

    @Transactional
    public LoyaltyAccount enrollIfNeeded(Guest guest, boolean enroll) {
        if (!enroll) {
            return guest.getLoyaltyAccount();
        }
        LoyaltyAccount account = guest.getLoyaltyAccount();
        if (account == null) {
            account = new LoyaltyAccount();
            account.setMembershipNumber(generateMembershipNumber());
            account.setPointsBalance(registry.loyalty().getEnrollmentBonus());
            account.setGuest(guest);
            account = accountRepository.save(account);
            guest.setLoyaltyAccount(account);
            addLedger(account, LoyaltyTransactionType.EARN, registry.loyalty().getEnrollmentBonus(),
                "Enrollment bonus", null);
        }
        return account;
    }

    @Transactional
    public BigDecimal redeemDiscount(Guest guest, BigDecimal subtotal) {
        LoyaltyAccount account = guest.getLoyaltyAccount();
        if (account == null || account.getPointsBalance() <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount = redemptionStrategy.redeem(
            subtotal,
            account.getPointsBalance(),
            registry.loyalty().getRedemptionRate(),
            registry.loyalty().getRedemptionCapPercent()
        );
        int pointsUsed = discount.divide(BigDecimal.valueOf(registry.loyalty().getRedemptionRate()),
            0, RoundingMode.HALF_UP).intValue();
        if (pointsUsed <= 0) {
            return BigDecimal.ZERO;
        }
        account.setPointsBalance(Math.max(0, account.getPointsBalance() - pointsUsed));
        accountRepository.save(account);
        addLedger(account, LoyaltyTransactionType.REDEEM, -pointsUsed, "Applied toward stay", null);
        return discount;
    }

    @Transactional
    public void earnPoints(Guest guest, BigDecimal amount, String reference) {
        LoyaltyAccount account = guest.getLoyaltyAccount();
        if (account == null) {
            return;
        }
        double earningRate = registry.loyalty().getEarningRate();
        int points = amount.multiply(BigDecimal.valueOf(earningRate))
            .setScale(0, RoundingMode.HALF_UP)
            .intValue();
        account.setPointsBalance(account.getPointsBalance() + points);
        accountRepository.save(account);
        addLedger(account, LoyaltyTransactionType.EARN, points, "Earned on payment", reference);
    }

    private String generateMembershipNumber() {
        return "BH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void addLedger(LoyaltyAccount account, LoyaltyTransactionType type, int points, String description, String reference) {
        LoyaltyLedgerEntry entry = new LoyaltyLedgerEntry();
        entry.setAccount(account);
        entry.setType(type);
        entry.setPoints(points);
        entry.setDescription(description);
        entry.setReferenceCode(reference);
        ledgerRepository.save(entry);
    }
}
