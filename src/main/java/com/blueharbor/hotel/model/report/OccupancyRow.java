package com.blueharbor.hotel.model.report;

import java.time.LocalDate;

public record OccupancyRow(
    LocalDate date,
    int roomsAvailable,
    int roomsOccupied,
    double occupancyPercentage
) {
}
