package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer>, QuerydslPredicateExecutor<Event> {

    @Query(" select e from Event as e " +
            "left join fetch e.initiator " +
            "left join fetch e.category " +
            "left join fetch e.location " +
            "where e.initiator.id = ?1 " +
            "order by e.id")
    List<Event> findAllByUserId(Integer userId);

    @Query(" select e from Event as e " +
            "left join fetch e.initiator " +
            "left join fetch e.category " +
            "left join fetch e.location " +
            "where e.initiator.id = ?1 and e.id = ?2")
    Optional<Event> findByUserIdAndEventId(Integer userId, Integer eventId);

    @Query(" select e from Event as e " +
            "left join fetch e.initiator " +
            "left join fetch e.category " +
            "left join fetch e.location " +
            "where e.id = ?1 and " +
            "e.state = 'PUBLISHED'")
    Optional<Event> findByIdPublic(Integer id);
}
