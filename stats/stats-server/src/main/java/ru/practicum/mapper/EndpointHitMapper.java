package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;

import java.sql.Timestamp;

@Component
public class EndpointHitMapper {
    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setId(endpointHitDto.getId());
        endpointHit.setNameService(endpointHitDto.getNameService());
        endpointHit.setUri(endpointHitDto.getUri());
        endpointHit.setUserIp(endpointHitDto.getUserIp());
        endpointHit.setTimestamp(Timestamp.valueOf(endpointHitDto.getTimestamp()));
        return endpointHit;
    }
}
