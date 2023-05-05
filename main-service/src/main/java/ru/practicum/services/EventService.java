package ru.practicum.services;

import ru.practicum.model.EventRequestStatusUpdateRequest;
import ru.practicum.model.EventRequestStatusUpdateResult;
import ru.practicum.model.Request;
import ru.practicum.model.dto.*;

import java.util.List;

public interface EventService {
    List<EventShortDto> findAllByUserId(Integer userId, Integer from, Integer size);

    EventFullDto create(Integer userId, NewEventDto newEventDto);

    EventFullDto findByUserIdAndEventId(Integer userId, Integer eventId);

    EventFullDto update(Integer userId, Integer eventId, UpdateEventRequest updateEventRequest);

    EventFullDto updateByAdmin(Integer eventId, UpdateEventRequest updateEventRequest);

    List<EventFullDto> findAllByParamForAdmin(Integer[] users, String[] states, Integer[] categories,
                                              String rangeStart, String rangeEnd, Integer from, Integer size);

    List<EventShortDto> findAllByParamPublic(String text, Integer[] categories, Boolean paid, String rangeStart,
                                             String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size);

    EventFullDto findByIdPublic(Integer id);

    List<Request> findAllRequestsByUserIdAndEventId(Integer userId, Integer eventId);

    EventRequestStatusUpdateResult updateRequestStatus(Integer userId, Integer eventId, EventRequestStatusUpdateRequest request);
}
