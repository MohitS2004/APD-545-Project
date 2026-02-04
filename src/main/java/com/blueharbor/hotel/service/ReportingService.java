package com.blueharbor.hotel.service;

import com.blueharbor.hotel.config.ConfigRegistry;
import com.blueharbor.hotel.dto.ActivityLogFilter;
import com.blueharbor.hotel.dto.OccupancyReportFilter;
import com.blueharbor.hotel.dto.RevenueReportFilter;
import com.blueharbor.hotel.model.Reservation;
import com.blueharbor.hotel.model.ReservedRoom;
import com.blueharbor.hotel.model.activity.ActivityLog;
import com.blueharbor.hotel.model.report.ActivityRow;
import com.blueharbor.hotel.model.report.OccupancyRow;
import com.blueharbor.hotel.model.report.RevenueRow;
import com.blueharbor.hotel.repository.ActivityLogRepository;
import com.blueharbor.hotel.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ReportingService {

    private final ReservationRepository reservationRepository;
    private final ActivityLogRepository activityLogRepository;
    private final ConfigRegistry registry;

    public ReportingService(
        ReservationRepository reservationRepository,
        ActivityLogRepository activityLogRepository,
        ConfigRegistry registry
    ) {
        this.reservationRepository = reservationRepository;
        this.activityLogRepository = activityLogRepository;
        this.registry = registry;
    }

    public List<RevenueRow> revenue(RevenueReportFilter filter) {
        List<Reservation> reservations = reservationRepository.findAll();
        Map<String, RevenueAccumulator> buckets = new HashMap<>();
        for (Reservation reservation : reservations) {
            if (filter.from() != null && reservation.getCheckInDate().isBefore(filter.from())) {
                continue;
            }
            if (filter.to() != null && reservation.getCheckOutDate().isAfter(filter.to())) {
                continue;
            }
            String key = bucketKey(reservation.getCheckInDate(), filter.granularity());
            RevenueAccumulator accumulator = buckets.computeIfAbsent(key, k -> new RevenueAccumulator(key));
            accumulator.add(reservation);
        }
        return buckets.values().stream()
            .sorted(Comparator.comparing(RevenueAccumulator::label))
            .map(RevenueAccumulator::toRow)
            .toList();
    }

    public List<OccupancyRow> occupancy(OccupancyReportFilter filter) {
        LocalDate start = filter.from();
        LocalDate end = filter.to();
        if (start == null || end == null) {
            throw new IllegalArgumentException("Date range required");
        }
        List<Reservation> reservations = reservationRepository.findAll();
        List<OccupancyRow> rows = new ArrayList<>();
        for (LocalDate cursor = start; !cursor.isAfter(end); cursor = cursor.plusDays(1)) {
            final LocalDate currentDate = cursor;
            int occupiedRooms = reservations.stream()
                .filter(res -> overlaps(res, currentDate))
                .mapToInt(res -> res.getRooms().stream()
                    .mapToInt(ReservedRoom::getQuantity)
                    .sum())
                .sum();
            int totalRooms = registry.hotel().getTotalRooms();
            double percent = totalRooms == 0 ? 0 : (occupiedRooms / (double) totalRooms) * 100;
            rows.add(new OccupancyRow(cursor, totalRooms, occupiedRooms,
                BigDecimal.valueOf(percent).setScale(2, RoundingMode.HALF_UP).doubleValue()));
        }
        return rows;
    }

    public List<ActivityRow> activity(ActivityLogFilter filter) {
        LocalDateTime from = filter.from();
        LocalDateTime to = filter.to();
        List<ActivityLog> logs;
        if (from != null && to != null) {
            logs = activityLogRepository.findByTimestampBetween(from, to);
        } else {
            logs = activityLogRepository.findAll();
        }
        return logs.stream()
            .sorted(Comparator.comparing(ActivityLog::getTimestamp).reversed())
            .map(log -> new ActivityRow(
                log.getTimestamp(),
                log.getActor(),
                log.getAction().name(),
                log.getEntityType(),
                log.getEntityIdentifier(),
                log.getMessage()
            ))
            .toList();
    }

    private record RevenueAccumulator(String label, List<Reservation> reservations) {
        RevenueAccumulator(String label) {
            this(label, new ArrayList<>());
        }

        void add(Reservation reservation) {
            reservations.add(reservation);
        }

        RevenueRow toRow() {
            BigDecimal subtotal = reservations.stream()
                .map(Reservation::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal tax = reservations.stream()
                .map(Reservation::getTaxes)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal discounts = reservations.stream()
                .map(Reservation::getDiscounts)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal total = reservations.stream()
                .map(Reservation::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            return new RevenueRow(label, null, null, reservations.size(), subtotal, tax, discounts, total);
        }
    }

    private String bucketKey(LocalDate date, String granularity) {
        if (granularity == null) {
            return date.toString();
        }
        return switch (granularity.toLowerCase()) {
            case "week" -> {
                WeekFields wf = WeekFields.of(Locale.getDefault());
                int week = date.get(wf.weekOfWeekBasedYear());
                yield date.getYear() + "-W" + week;
            }
            case "month" -> date.getYear() + "-" + date.getMonthValue();
            default -> date.toString();
        };
    }

    private boolean overlaps(Reservation reservation, LocalDate date) {
        return !reservation.getCheckInDate().isAfter(date) && reservation.getCheckOutDate().isAfter(date);
    }
}
