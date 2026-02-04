package com.blueharbor.hotel.dto;

import com.blueharbor.hotel.model.RoomType;
import com.blueharbor.hotel.model.addon.AddOnCode;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record ReservationRequest(
    String firstName,
    String lastName,
    String email,
    String phone,
    int adults,
    int children,
    LocalDate checkIn,
    LocalDate checkOut,
    Map<RoomType, Integer> roomCounts,
    List<AddOnCode> addOns,
    boolean enrollInLoyalty,
    String notes
) {
}
