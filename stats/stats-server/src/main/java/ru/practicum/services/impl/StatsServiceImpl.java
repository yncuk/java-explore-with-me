package ru.practicum.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.services.StatsService;
import ru.practicum.model.ViewStats;
import ru.practicum.repositories.EndpointHitRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final EndpointHitRepository endpointHitRepository;

    @Override
    public EndpointHit create(EndpointHitDto endpointHitDto) {
        return endpointHitRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
    }

    @Override
    public List<ViewStats> search(String start, String end, String[] uris, Boolean unique) {
        Timestamp startTime = parseStringDate(start);
        Timestamp endTime = parseStringDate(end);
        List<ViewStats> viewStats = new ArrayList<>();
        List<EndpointHit> list;
        if (uris != null) {
            for (String currentUri : uris) {
                if (unique != null) {
                    if (unique) {
                        list = endpointHitRepository.getAllEndpointHitInTimeUniqueIp(startTime, endTime, currentUri);
                    } else {
                        list = endpointHitRepository.getAllEndpointHitInTimeByUri(startTime, endTime, currentUri);
                    }
                } else {
                    list = endpointHitRepository.getAllEndpointHitInTimeByUri(startTime, endTime, currentUri);
                }
                ViewStats currentViewStats = new ViewStats();
                currentViewStats.setApp(list.get(0).getNameService());
                currentViewStats.setUri(currentUri);
                currentViewStats.setHits(list.size());
                viewStats.add(currentViewStats);
            }
        } else {
            list = endpointHitRepository.getAllEndpointHitInTime(startTime, endTime);
            for (EndpointHit currentEndpointHit : list) {
                ViewStats currentViewStats = new ViewStats();
                currentViewStats.setApp(currentEndpointHit.getNameService());
                currentViewStats.setUri(currentEndpointHit.getUri());
                currentViewStats.setHits(1);
                viewStats.add(currentViewStats);
            }
        }
        viewStats.sort(Comparator.comparing(ViewStats::getHits).reversed());
        return viewStats;
    }

    private Timestamp parseStringDate(String date) {
        return Timestamp.valueOf(date);
    }
}
