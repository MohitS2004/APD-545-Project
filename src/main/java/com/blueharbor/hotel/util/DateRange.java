package com.blueharbor.hotel.util;

import java.time.LocalDate;

public record DateRange(LocalDate start, LocalDate end) {

    public boolean overlaps(DateRange other) {
        return start().isBefore(other.end()) && end().isAfter(other.start());
    }

    public long nights() {
        return end.toEpochDay() - start.toEpochDay();
    }
}
