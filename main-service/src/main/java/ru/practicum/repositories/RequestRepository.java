package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Request;
import ru.practicum.model.enums.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    Optional<Request> findByRequesterAndEvent(Integer userId, Integer eventId);

    List<Request> findByEvent(Integer eventId);

    List<Request> findByEventAndStatus(Integer eventId, RequestStatus status);

    List<Request> findByRequester(Integer userId);

    Optional<Request> findByRequesterAndId(Integer userId, Integer requestId);
}
