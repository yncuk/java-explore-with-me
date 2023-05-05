package ru.practicum.services;

import ru.practicum.model.Request;

import java.util.List;

public interface RequestService {
    Request create(Integer userId, Integer eventId);

    List<Request> findAllByUserId(Integer userId);

    Request delete(Integer userId, Integer requestId);
}
