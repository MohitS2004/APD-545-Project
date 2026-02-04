package com.blueharbor.hotel.service;

import com.blueharbor.hotel.dto.PaymentCommand;
import com.blueharbor.hotel.logging.ActivityLogger;
import com.blueharbor.hotel.model.Reservation;
import com.blueharbor.hotel.model.ReservationStatus;
import com.blueharbor.hotel.model.activity.ActivityAction;
import com.blueharbor.hotel.model.billing.Payment;
import com.blueharbor.hotel.model.billing.PaymentMethod;
import com.blueharbor.hotel.observer.ReservationEvent;
import com.blueharbor.hotel.observer.ReservationEventPublisher;
import com.blueharbor.hotel.observer.ReservationEventType;
import com.blueharbor.hotel.repository.PaymentRepository;
import com.blueharbor.hotel.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final LoyaltyService loyaltyService;
    private final ReservationEventPublisher eventPublisher;
    private final ActivityLogger activityLogger;

    public PaymentService(
        PaymentRepository paymentRepository,
        ReservationRepository reservationRepository,
        LoyaltyService loyaltyService,
        ReservationEventPublisher eventPublisher,
        ActivityLogger activityLogger
    ) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.loyaltyService = loyaltyService;
        this.eventPublisher = eventPublisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public Payment process(PaymentCommand command) {
        Reservation reservation = reservationRepository.findById(command.reservationId())
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(command.amount());
        payment.setMethod(command.method());
        payment.setRefund(command.refund());
        payment.setActorEmail(command.actorEmail());
        payment.setReference(command.reference());

        if (command.refund()) {
            applyRefund(reservation, command.amount());
        } else {
            BigDecimal effectiveAmount = command.amount();
            if (command.method() == PaymentMethod.LOYALTY_POINTS) {
                effectiveAmount = loyaltyService.redeemDiscount(reservation.getGuest(), reservation.getTotal());
                payment.setAmount(effectiveAmount);
            }
            applyPayment(reservation, effectiveAmount);
            if (command.method() != PaymentMethod.LOYALTY_POINTS) {
                loyaltyService.earnPoints(reservation.getGuest(), command.amount(), reservation.getConfirmationCode());
            }
        }
        reservationRepository.save(reservation);

        Payment saved = paymentRepository.save(payment);
        eventPublisher.publish(ReservationEvent.of(ReservationEventType.PAYMENT_CAPTURED, reservation.getId(),
            "Payment " + command.method() + " " + command.amount()));
        activityLogger.log(ActivityAction.PAYMENT_CAPTURED, command.actorEmail(), "Reservation",
            reservation.getConfirmationCode(), "Processed payment");
        return saved;
    }

    public BigDecimal outstandingBalance(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow();
        BigDecimal paid = reservation.getPayments().stream()
            .map(payment -> payment.isRefund() ? payment.getAmount().negate() : payment.getAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return reservation.getTotal().subtract(paid);
    }

    public java.util.List<Payment> listPayments() {
        return paymentRepository.findAll();
    }

    private void applyPayment(Reservation reservation, BigDecimal amount) {
        reservation.setDepositPaid(reservation.getDepositPaid().add(amount));
        if (reservation.getDepositPaid().compareTo(reservation.getDepositRequired()) >= 0
            && reservation.getStatus() == ReservationStatus.DRAFT) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
        }
    }

    private void applyRefund(Reservation reservation, BigDecimal amount) {
        reservation.setDepositPaid(reservation.getDepositPaid().subtract(amount));
    }
}
