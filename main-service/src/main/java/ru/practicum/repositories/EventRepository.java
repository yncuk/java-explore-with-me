package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer>, QuerydslPredicateExecutor<Event> {

    @Query(" select e from Event as e " +
            "left join fetch e.initiator " +
            "left join fetch e.category " +
            "left join fetch e.location " +
            "where e.initiator.id = :userId " +
            "order by e.id")
    List<Event> findAllByUserId(@Param("userId") Integer userId);

    @Query(" select e from Event as e " +
            "left join fetch e.initiator " +
            "left join fetch e.category " +
            "left join fetch e.location " +
            "where e.initiator.id = :userId and e.id = :eventId")
    Optional<Event> findByUserIdAndEventId(@Param("userId") Integer userId, @Param("eventId") Integer eventId);

    @Query(" select e from Event as e " +
            "left join fetch e.initiator " +
            "left join fetch e.category " +
            "left join fetch e.location " +
            "where e.id = :id and " +
            "e.state = 'PUBLISHED'")
    Optional<Event> findByIdPublic(@Param("id") Integer id);
}
