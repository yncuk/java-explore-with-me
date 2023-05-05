package ru.practicum.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.EntityBadRequestException;
import ru.practicum.exceptions.EntityForbiddenException;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.enums.RequestStatus;
import ru.practicum.model.enums.State;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.RequestRepository;
import ru.practicum.repositories.UserRepository;
import ru.practicum.services.RequestService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public Request create(Integer userId, Integer eventId) {
        validateId(userId);
        validateId(eventId);
        if (requestRepository.findByRequesterAndEvent(userId, eventId).isPresent()) {
            throw new EntityForbiddenException("Нельзя добавить повторный запрос");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден пользователь с ID = %s", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден событие с ID = %s", eventId)));
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new EntityForbiddenException("Инициатор события не может добавить запрос на участие в своем событии");
        }
        if (event.getState() == null) {
            throw new EntityForbiddenException("Нельзя участвовать в неопубликованном событии");
        } else if (!event.getState().equals(State.PUBLISHED)) {
            throw new EntityForbiddenException("Нельзя участвовать в неопубликованном событии");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() == requestRepository.findByEventAndStatus(eventId, RequestStatus.CONFIRMED).size()) {
            throw new EntityForbiddenException("Достигнут лимит запросов на участие");
        }
        Request newRequest = new Request();
        newRequest.setCreated(LocalDateTime.now().format(FORMATTER));
        newRequest.setEvent(eventId);
        newRequest.setRequester(userId);
        if (!event.getRequestModeration()) {
            newRequest.setStatus(RequestStatus.CONFIRMED);
            if (event.getConfirmedRequests() == null) {
                event.setConfirmedRequests(1);
            } else {
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            }
        } else {
            newRequest.setStatus(RequestStatus.PENDING);
        }
        eventRepository.save(event);
        return requestRepository.save(newRequest);
    }

    @Override
    public List<Request> findAllByUserId(Integer userId) {
        validateId(userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден пользователь с ID = %s", userId)));
        return requestRepository.findByRequester(userId);
    }

    @Override
    public Request delete(Integer userId, Integer requestId) {
        validateId(userId);
        validateId(requestId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден пользователь с ID = %s", userId)));
        Request request = requestRepository.findByRequesterAndId(userId, requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Для пользователя с ID = %s не найден запрос с ID = %s", userId, requestId)));
        request.setStatus(RequestStatus.CANCELED);
        requestRepository.deleteById(requestId);
        return request;
    }

    private void validateId(Integer id) {
        if (id <= 0) {
            throw new EntityBadRequestException("ID должен быть больше 0");
        }
    }
}
