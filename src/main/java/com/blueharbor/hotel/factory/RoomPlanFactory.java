package com.blueharbor.hotel.factory;

import com.blueharbor.hotel.config.ConfigRegistry;
import com.blueharbor.hotel.dto.RoomPlanSuggestion;
import com.blueharbor.hotel.model.RoomType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class RoomPlanFactory {

    private final ConfigRegistry configRegistry;

    public RoomPlanFactory(ConfigRegistry configRegistry) {
        this.configRegistry = configRegistry;
    }

    public RoomPlanSuggestion suggest(int adults, int children) {
        Map<RoomType, Integer> rooms = new EnumMap<>(RoomType.class);
        int remainingAdults = adults;

        while (remainingAdults >= 4) {
            rooms.merge(RoomType.DOUBLE, 1, Integer::sum);
            remainingAdults -= 4;
        }

        if (remainingAdults == 3) {
            rooms.merge(RoomType.DOUBLE, 1, Integer::sum);
            remainingAdults = 0;
        } else if (remainingAdults > 0) {
            rooms.merge(RoomType.SINGLE, 1, Integer::sum);
            remainingAdults = 0;
        }

        int totalCapacity = capacity(rooms);
        int extraPeople = adults + children - totalCapacity;
        while (extraPeople > 0) {
            rooms.merge(RoomType.SINGLE, 1, Integer::sum);
            extraPeople -= configRegistry.occupancy().getSingle();
        }

        String rationale = "Auto-suggested based on occupancy policies";
        return new RoomPlanSuggestion(rooms, capacity(rooms), rationale);
    }

    private int capacity(Map<RoomType, Integer> rooms) {
        return rooms.entrySet().stream()
            .mapToInt(entry -> entry.getValue() * configRegistry.occupancyLimit(entry.getKey().name()))
            .sum();
    }
}
