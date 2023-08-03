package ru.server.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.statisticServer.dto.StatisticRequest;
import ru.server.model.EndpointHitEntity;

import java.util.List;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class StatisticMapper {

    public EndpointHitEntity toEndpointHitEntity(StatisticRequest statisticRequest) {
        return EndpointHitEntity
                .builder()
                .app(statisticRequest.getApp())
                .uri(statisticRequest.getUri())
                .ip(statisticRequest.getIp())
                .createdOn(statisticRequest.getCreatedOn())
                .build();
    }

    public List<EndpointHitEntity> toEndpointHitEntityList(StatisticRequest statisticRequest) {
        return statisticRequest
                .getEventsIds()
                .stream()
                .map(eventId -> EndpointHitEntity
                        .builder()
                        .id(statisticRequest.getId())
                        .app(statisticRequest.getApp())
                        .uri(statisticRequest.getUri() + "/" + eventId)
                        .ip(statisticRequest.getIp())
                        .createdOn(statisticRequest.getCreatedOn())
                        .build())
                .collect(toList());
    }

    public StatisticRequest toStatisticRequest(EndpointHitEntity endpointHitEntity, List<Long> eventIds) {
        return StatisticRequest
                .builder()
                .id(endpointHitEntity.getId())
                .app(endpointHitEntity.getApp())
                .uri(endpointHitEntity.getUri())
                .eventsIds(eventIds)
                .ip(endpointHitEntity.getIp())
                .createdOn(endpointHitEntity.getCreatedOn())
                .build();
    }

}
