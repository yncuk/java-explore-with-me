package ru.practicum.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.services.StatsService;
import ru.practicum.model.ViewStats;
import ru.practicum.repositories.EndpointHitRepository;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final EndpointHitRepository endpointHitRepository;

    @Transactional
    @Override
    public EndpointHit create(EndpointHitDto endpointHitDto) {
        return endpointHitRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
    }

    @Override
    public List<ViewStats> search(String start, String end, String[] uris, Boolean unique) {
        Timestamp startTime = parseStringDate(start);
        Timestamp endTime = parseStringDate(end);

        Set<ViewStats> viewStats = new HashSet<>();
        List<String> list;
        if (uris != null) {
            if (unique != null && unique) {
                list = endpointHitRepository.getAllEndpointHitInTimeUniqueIp(startTime, endTime, uris);
            } else {
                list = endpointHitRepository.getAllEndpointHitInTimeByUri(startTime, endTime, uris);
            }
            for (String currentEndpointHit : list) {
                ViewStats currentViewStats = new ViewStats();
                currentViewStats.setApp("ewm-main-service");
                currentViewStats.setUri(currentEndpointHit);
                currentViewStats.setHits(Collections.frequency(list, currentEndpointHit));
                viewStats.add(currentViewStats);
            }
        } else {
            if (unique != null && unique) {
                list = endpointHitRepository.getAllEndpointHitInTimeUniqueIp(startTime, endTime);
            } else {
                list = endpointHitRepository.getAllEndpointHitInTime(startTime, endTime);
            }
            for (String currentEndpointHit : list) {
                ViewStats currentViewStats = new ViewStats();
                currentViewStats.setApp("ewm-main-service");
                currentViewStats.setUri(currentEndpointHit);
                currentViewStats.setHits(Collections.frequency(list, currentEndpointHit));
                viewStats.add(currentViewStats);
            }
        }
        return viewStats.stream().sorted(Comparator.comparing(ViewStats::getHits).reversed()).collect(Collectors.toList());
    }

    private Timestamp parseStringDate(String date) {
        return Timestamp.valueOf(date);
    }
}
