package com.blueharbor.hotel.repository;

import com.blueharbor.hotel.model.addon.ReservationAddOn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationAddOnRepository extends JpaRepository<ReservationAddOn, UUID> {
}
