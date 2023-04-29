package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;

import java.sql.Timestamp;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query(" select eh.uri from EndpointHit as eh " +
            "where eh.timestamp >= ?1 and " +
            "eh.timestamp <= ?2")
    List<String> getAllEndpointHitInTime(Timestamp start, Timestamp end);

    @Query(value = " select distinct on (eh.ip) eh.uri from endpoint_hit as eh " +
            "where eh.timestamp >= ?1 and " +
            "eh.timestamp <= ?2", nativeQuery = true)
    List<String> getAllEndpointHitInTimeUniqueIp(Timestamp start, Timestamp end);

    @Query(" select eh.uri from EndpointHit as eh " +
            "where eh.timestamp >= ?1 and " +
            "eh.timestamp <= ?2 and " +
            "eh.uri in ?3")
    List<String> getAllEndpointHitInTimeByUri(Timestamp start, Timestamp end, String[] uris);

    @Query(value = " select distinct on (eh.ip) eh.uri from endpoint_hit as eh " +
            "where eh.timestamp >= ?1 and " +
            "eh.timestamp <= ?2 and " +
            "eh.uri in ?3", nativeQuery = true)
    List<String> getAllEndpointHitInTimeUniqueIp(Timestamp start, Timestamp end, String[] uris);

}
