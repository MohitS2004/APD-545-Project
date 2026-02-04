package com.blueharbor.hotel.dto;

import com.blueharbor.hotel.model.RoomType;

import java.time.LocalDate;

public record WaitlistRequest(
    String guestName,
    String phone,
    String email,
    RoomType roomType,
    LocalDate startDate,
    LocalDate endDate,
    String notes
) {
}
