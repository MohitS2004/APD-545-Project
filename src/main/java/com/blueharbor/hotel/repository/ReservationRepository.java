package com.blueharbor.hotel.repository;

import com.blueharbor.hotel.model.Reservation;
import com.blueharbor.hotel.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    Optional<Reservation> findByConfirmationCode(String confirmationCode);

    @Query("""
        select r from Reservation r
        where (:status is null or r.status = :status)
          and (:guest is null or lower(concat(r.guest.firstName,' ', r.guest.lastName)) like lower(concat('%', :guest, '%')))
          and (:phone is null or r.guest.phone like concat('%', :phone, '%'))
          and (:fromDate is null or r.checkInDate >= :fromDate)
          and (:toDate is null or r.checkOutDate <= :toDate)
        order by r.checkInDate desc
        """)
    List<Reservation> search(
        @Param("status") ReservationStatus status,
        @Param("guest") String guest,
        @Param("phone") String phone,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate
    );

    @Query("""
        select r from Reservation r
        where r.status in ('CONFIRMED','CHECKED_IN')
          and r.checkInDate < :endDate
          and r.checkOutDate > :startDate
        """)
    List<Reservation> findActiveReservationsBetween(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);
}
