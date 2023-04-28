package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;

import java.sql.Timestamp;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query(" select eh from EndpointHit as eh " +
            "where eh.timestamp >= ?1 and " +
            "eh.timestamp <= ?2")
    List<EndpointHit> getAllEndpointHitInTime(Timestamp start, Timestamp end);

    @Query(" select eh from EndpointHit as eh " +
            "where eh.timestamp >= ?1 and " +
            "eh.timestamp <= ?2 and " +
            "eh.uri = ?3")
    List<EndpointHit> getAllEndpointHitInTimeByUri(Timestamp start, Timestamp end, String currentUri);

    @Query(value = " select distinct on (eh.user_ip) eh.* from endpoint_hit as eh " +
            "where eh.timestamp >= ?1 and " +
            "eh.timestamp <= ?2 and " +
            "eh.uri = ?3", nativeQuery = true)
    List<EndpointHit> getAllEndpointHitInTimeUniqueIp(Timestamp start, Timestamp end, String currentUri);

}
