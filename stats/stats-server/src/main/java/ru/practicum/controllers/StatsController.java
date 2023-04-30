package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.services.StatsService;
import ru.practicum.model.ViewStats;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHit> create(@RequestBody EndpointHitDto endpointHitDto) {
        return new ResponseEntity<>(statsService.create(endpointHitDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStats>> search(@RequestParam String start,
                                                  @RequestParam String end,
                                                  @RequestParam(required = false) String[] uris,
                                                  @RequestParam(required = false) Boolean unique) {
        return ResponseEntity.ok(statsService.search(start, end, uris, unique));
    }
}
