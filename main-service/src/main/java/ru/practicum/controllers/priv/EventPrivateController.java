package ru.practicum.controllers.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.EventRequestStatusUpdateRequest;
import ru.practicum.model.EventRequestStatusUpdateResult;
import ru.practicum.model.Request;
import ru.practicum.model.dto.EventFullDto;
import ru.practicum.model.dto.EventShortDto;
import ru.practicum.model.dto.NewEventDto;
import ru.practicum.model.dto.UpdateEventRequest;
import ru.practicum.services.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class EventPrivateController {

    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> findAllByUserId(@PathVariable Integer userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        return eventService.findAllByUserId(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> create(@PathVariable Integer userId,
                                               @RequestBody @Valid NewEventDto newEventDto) {
        return new ResponseEntity<>(eventService.create(userId, newEventDto), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto findByUserIdAndEventId(@PathVariable Integer userId,
                                               @PathVariable Integer eventId) {
        return eventService.findByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto update(@PathVariable Integer userId,
                               @PathVariable Integer eventId,
                               @RequestBody @Valid UpdateEventRequest updateEventRequest) {
        return eventService.update(userId, eventId, updateEventRequest);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<Request> findAllRequestsByUserIdAndEventId(@PathVariable Integer userId,
                                                           @PathVariable Integer eventId) {
        return eventService.findAllRequestsByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Integer userId,
                                                              @PathVariable Integer eventId,
                                                              @RequestBody @Valid EventRequestStatusUpdateRequest request) {
        return eventService.updateRequestStatus(userId, eventId, request);
    }
}
