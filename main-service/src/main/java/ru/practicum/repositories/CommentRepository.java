package ru.practicum.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Comment;
import ru.practicum.model.CounterComments;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer>, QuerydslPredicateExecutor<Comment> {

    List<Comment> findAllByEventId(Integer eventId, Pageable pageable);

    Optional<Comment> findByAuthor_IdAndId(Integer userId, Integer commentId);

    @Query(" select new ru.practicum.model.CounterComments(count(c), c.eventId) from Comment as c " +
            "left join Event as e on c.eventId = e.id " +
            "where c.eventId in :eventId " +
            "group by c.eventId")
    Optional<CounterComments> getCountCommentsByEventId(@Param("eventId") Integer eventId);

    @Query(" select new ru.practicum.model.CounterComments(count(c), c.eventId) from Comment as c " +
            "left join Event as e on c.eventId = e.id " +
            "where e in :event " +
            "group by c.eventId")
    List<CounterComments> getCountCommentsByEvent(@Param("event") List<Event> event);
}
