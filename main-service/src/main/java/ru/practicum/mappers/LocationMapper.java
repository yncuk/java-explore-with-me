package ru.practicum.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.model.Location;
import ru.practicum.model.dto.LocationDto;

@Component
public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(
                location.getLat(),
                location.getLon());
    }
}
