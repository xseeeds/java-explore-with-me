package ru.server.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.statisticServer.dto.StatisticRequest;
import ru.server.model.EndpointHitEntity;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@UtilityClass
public class StatisticMapper {

//TODO if else EndpointHitEntity можно сделать с листом eventIds, если запрос /events, приложить к нему список eventIds

    public EndpointHitEntity toEndpointHitEntity(StatisticRequest statisticRequest) {
        if (!statisticRequest.getUri().equals("/events")) {
            return EndpointHitEntity
                    .builder()
                    .app(statisticRequest.getApp())
                    .uri(statisticRequest.getUri())
                    .eventId(Long.parseLong(statisticRequest.getUri()
                            .substring(statisticRequest.getUri().lastIndexOf("/") + 1)))
                    .ip(statisticRequest.getIp())
                    .createdOn(statisticRequest.getCreatedOn())
                    .build();
        } else {
            return EndpointHitEntity
                    .builder()
                    .app(statisticRequest.getApp())
                    .uri(statisticRequest.getUri())
                    .ip(statisticRequest.getIp())
                    .createdOn(statisticRequest.getCreatedOn())
                    .build();
        }
    }

    public List<EndpointHitEntity> toEndpointHitEntityList(StatisticRequest statisticRequest) {
        return statisticRequest
                .getEventsIds()
                .stream()
                .map(eventId -> EndpointHitEntity
                        .builder()
                        .app(statisticRequest.getApp())
                        .uri(statisticRequest.getUri())
                        .eventId(eventId)
                        .ip(statisticRequest.getIp())
                        .createdOn(statisticRequest.getCreatedOn())
                        .build())
                .collect(toList());
    }

    public List<StatisticRequest> toStatisticRequestList(EndpointHitEntity endpointHitEntity) {
        return List.of(
                StatisticRequest
                        .builder()
                        .id(endpointHitEntity.getId())
                        .app(endpointHitEntity.getApp())
                        .uri(endpointHitEntity.getUri())
                        .eventsIds(emptyList())
                        .ip(endpointHitEntity.getIp())
                        .createdOn(endpointHitEntity.getCreatedOn())
                        .build());
    }

    public List<StatisticRequest> toStatisticRequestList(List<EndpointHitEntity> endpointHitEntity, List<Long> eventsIds) {
        return endpointHitEntity
                .stream()
                .map(endpointHit -> StatisticRequest
                        .builder()
                        .id(endpointHit.getId())
                        .app(endpointHit.getApp())
                        .uri(endpointHit.getUri())
                        .eventsIds(eventsIds)
                        .ip(endpointHit.getIp())
                        .createdOn(endpointHit.getCreatedOn())
                        .build())
                .collect(toList());
    }

}
