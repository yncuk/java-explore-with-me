package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.model.Location;
import ru.practicum.model.dto.LocationDto;

@UtilityClass
public class LocationMapper {
    public LocationDto toLocationDto(Location location) {
        return new LocationDto(
                location.getLat(),
                location.getLon());
    }
}
