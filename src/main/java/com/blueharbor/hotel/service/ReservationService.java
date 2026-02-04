package com.blueharbor.hotel.service;

import com.blueharbor.hotel.config.ConfigRegistry;
import com.blueharbor.hotel.dto.ReservationEstimate;
import com.blueharbor.hotel.dto.ReservationRequest;
import com.blueharbor.hotel.dto.RoomPlanSuggestion;
import com.blueharbor.hotel.factory.RoomPlanFactory;
import com.blueharbor.hotel.model.Guest;
import com.blueharbor.hotel.model.Reservation;
import com.blueharbor.hotel.model.ReservationStatus;
import com.blueharbor.hotel.model.ReservedRoom;
import com.blueharbor.hotel.model.RoomType;
import com.blueharbor.hotel.model.addon.AddOnCode;
import com.blueharbor.hotel.model.addon.ReservationAddOn;
import com.blueharbor.hotel.observer.ReservationEvent;
import com.blueharbor.hotel.observer.ReservationEventPublisher;
import com.blueharbor.hotel.observer.ReservationEventType;
import com.blueharbor.hotel.repository.GuestRepository;
import com.blueharbor.hotel.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final PricingService pricingService;
    private final RoomPlanFactory roomPlanFactory;
    private final ConfigRegistry configRegistry;
    private final ReservationEventPublisher eventPublisher;
    private final LoyaltyService loyaltyService;

    public ReservationService(
        ReservationRepository reservationRepository,
        GuestRepository guestRepository,
        PricingService pricingService,
        RoomPlanFactory roomPlanFactory,
        ConfigRegistry configRegistry,
        ReservationEventPublisher eventPublisher,
        LoyaltyService loyaltyService
    ) {
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.pricingService = pricingService;
        this.roomPlanFactory = roomPlanFactory;
        this.configRegistry = configRegistry;
        this.eventPublisher = eventPublisher;
        this.loyaltyService = loyaltyService;
    }

    public RoomPlanSuggestion suggestPlan(int adults, int children) {
        return roomPlanFactory.suggest(adults, children);
    }

    public ReservationEstimate estimate(ReservationRequest request) {
        Map<RoomType, Integer> rooms = resolveRooms(request);
        ReservationRequest normalized = new ReservationRequest(
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phone(),
            request.adults(),
            request.children(),
            request.checkIn(),
            request.checkOut(),
            rooms,
            request.addOns(),
            request.enrollInLoyalty(),
            request.notes()
        );
        validate(normalized);
        return pricingService.estimate(normalized);
    }

    @Transactional
    public Reservation createReservation(ReservationRequest request) {
        Map<RoomType, Integer> rooms = resolveRooms(request);
        ReservationRequest normalized = new ReservationRequest(
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phone(),
            request.adults(),
            request.children(),
            request.checkIn(),
            request.checkOut(),
            rooms,
            request.addOns(),
            request.enrollInLoyalty(),
            request.notes()
        );
        validate(normalized);
        ReservationEstimate estimate = pricingService.estimate(normalized);

        Guest guest = upsertGuest(normalized);
        loyaltyService.enrollIfNeeded(guest, normalized.enrollInLoyalty());

        Reservation reservation = mapReservation(normalized, estimate, guest);
        Reservation saved = reservationRepository.save(reservation);
        eventPublisher.publish(ReservationEvent.of(ReservationEventType.CREATED, saved.getId(),
            "Reservation confirmed for " + guest.getFirstName()));
        return saved;
    }

    public List<Reservation> search(ReservationStatus status, String guestName, String phone,
                                    LocalDate from, LocalDate to) {
        return reservationRepository.search(status, guestName, phone, from, to);
    }

    @Transactional
    public Reservation checkout(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow();
        BigDecimal paid = reservation.getPayments().stream()
            .map(payment -> payment.isRefund() ? payment.getAmount().negate() : payment.getAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal outstanding = reservation.getTotal().subtract(paid);
        if (outstanding.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Balance remains. Settle payments before checkout.");
        }
        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        Reservation saved = reservationRepository.save(reservation);
        loyaltyService.earnPoints(saved.getGuest(), saved.getTotal(), saved.getConfirmationCode());
        eventPublisher.publish(ReservationEvent.of(ReservationEventType.CHECKED_OUT, reservationId,
            "Guest checked out"));
        return saved;
    }

    @Transactional
    public void cancel(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow();
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        eventPublisher.publish(ReservationEvent.of(ReservationEventType.CANCELLED, reservationId,
            "Reservation cancelled"));
    }

    private Guest upsertGuest(ReservationRequest request) {
        Optional<Guest> existing = guestRepository.findByEmail(request.email());
        Guest guest = existing.orElseGet(Guest::new);
        guest.setFirstName(request.firstName());
        guest.setLastName(request.lastName());
        guest.setEmail(request.email());
        guest.setPhone(request.phone());
        return guestRepository.save(guest);
    }

    private Reservation mapReservation(ReservationRequest request, ReservationEstimate estimate, Guest guest) {
        Reservation reservation = new Reservation();
        reservation.setGuest(guest);
        reservation.setAdults(request.adults());
        reservation.setChildren(request.children());
        reservation.setCheckInDate(request.checkIn());
        reservation.setCheckOutDate(request.checkOut());
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setSubtotal(estimate.subtotal());
        reservation.setTaxes(estimate.tax());
        reservation.setTotal(estimate.total());
        reservation.setDepositRequired(estimate.total().multiply(BigDecimal.valueOf(0.2)));
        reservation.setConfirmationCode(generateConfirmationCode(request));
        reservation.setNotes(request.notes());

        request.roomCounts().forEach((type, qty) -> {
            ReservedRoom reservedRoom = new ReservedRoom();
            reservedRoom.setRoomType(type);
            reservedRoom.setQuantity(qty);
            reservedRoom.setGuestsSupported(configRegistry.occupancyLimit(type.name()) * qty);
            reservedRoom.setNightlyRate(configRegistry.pricing().getBaseRates().get(type.name()));
            reservedRoom.setReservation(reservation);
            reservation.getRooms().add(reservedRoom);
        });

        List<AddOnCode> addOns = request.addOns() == null ? List.of() : request.addOns();
        addOns.forEach(code -> {
            ReservationAddOn addOn = new ReservationAddOn();
            addOn.setCode(code);
            addOn.setName(code.name());
            addOn.setReservation(reservation);
            addOn.setPerNight(configRegistry.addOns().get(code.name()).isPerNight());
            addOn.setNights((int) (request.checkOut().toEpochDay() - request.checkIn().toEpochDay()));
            addOn.setUnitPrice(configRegistry.addOns().get(code.name()).getPrice());
            reservation.getAddOns().add(addOn);
        });
        return reservation;
    }

    private Map<RoomType, Integer> resolveRooms(ReservationRequest request) {
        if (request.roomCounts() != null && !request.roomCounts().isEmpty()) {
            return request.roomCounts();
        }
        return roomPlanFactory.suggest(request.adults(), request.children()).roomCounts();
    }

    private void validate(ReservationRequest request) {
        if (request.checkIn().isAfter(request.checkOut()) || request.checkIn().isEqual(request.checkOut())) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }
        if (request.adults() < 1) {
            throw new IllegalArgumentException("At least one adult is required.");
        }
        int capacity = request.roomCounts().entrySet().stream()
            .mapToInt(entry -> entry.getValue() * configRegistry.occupancyLimit(entry.getKey().name()))
            .sum();
        if (capacity < request.adults() + request.children()) {
            throw new IllegalArgumentException("Selected rooms do not satisfy occupancy requirements.");
        }
    }

    private String generateConfirmationCode(ReservationRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return "BH-" + formatter.format(request.checkIn()) + "-" + UUID.randomUUID().toString().substring(0, 4);
    }
}
