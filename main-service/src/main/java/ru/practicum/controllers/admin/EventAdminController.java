package ru.practicum.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.EventFullDto;
import ru.practicum.model.dto.UpdateEventRequest;
import ru.practicum.services.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class EventAdminController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> findAllByParamForAdmin(@RequestParam(required = false) Integer[] users,
                                                     @RequestParam(required = false) String[] states,
                                                     @RequestParam(required = false) Integer[] categories,
                                                     @RequestParam(required = false) String rangeStart,
                                                     @RequestParam(required = false) String rangeEnd,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        return eventService.findAllByParamForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateByAdmin(@PathVariable Integer eventId,
                                      @RequestBody @Valid UpdateEventRequest updateEventRequest) {
        return eventService.updateByAdmin(eventId, updateEventRequest);
    }
}
