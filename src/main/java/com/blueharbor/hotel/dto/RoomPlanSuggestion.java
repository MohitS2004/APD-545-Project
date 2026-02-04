package com.blueharbor.hotel.dto;

import com.blueharbor.hotel.model.RoomType;

import java.util.Map;

public record RoomPlanSuggestion(
    Map<RoomType, Integer> roomCounts,
    int totalCapacity,
    String rationale
) {
}
