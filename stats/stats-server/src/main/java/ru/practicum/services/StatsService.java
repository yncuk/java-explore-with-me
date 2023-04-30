package ru.practicum.services;

import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.util.List;

public interface StatsService {
    EndpointHit create(EndpointHitDto endpointHitDto);

    List<ViewStats> search(String start, String end, String[] uris, Boolean unique);
}
