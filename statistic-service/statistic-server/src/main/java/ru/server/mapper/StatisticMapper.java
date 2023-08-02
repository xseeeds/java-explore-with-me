package ru.server.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.statisticServer.dto.StatisticRequest;
import ru.server.model.EndpointHitEntity;

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

    public StatisticRequest toStatisticRequest(EndpointHitEntity endpointHitEntity) {
        return StatisticRequest
                .builder()
                .id(endpointHitEntity.getId())
                .app(endpointHitEntity.getApp())
                .uri(endpointHitEntity.getUri())
                .ip(endpointHitEntity.getIp())
                .createdOn(endpointHitEntity.getCreatedOn())
                .build();
    }

}
