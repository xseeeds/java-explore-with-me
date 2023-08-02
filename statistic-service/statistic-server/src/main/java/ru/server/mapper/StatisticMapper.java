package ru.server.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.statisticServer.dto.StatisticDto;
import ru.server.model.EndpointHitEntity;

@UtilityClass
public class StatisticMapper {

    public EndpointHitEntity toEndpointHitEntity(StatisticDto statisticDto) {
        return EndpointHitEntity
                .builder()
                .app(statisticDto.getApp())
                .uri(statisticDto.getUri())
                .ip(statisticDto.getIp())
                .createdOn(statisticDto.getCreatedOn())
                .build();
    }

    public StatisticDto toStatisticDto(EndpointHitEntity endpointHitEntity) {
        return StatisticDto
                .builder()
                .id(endpointHitEntity.getId())
                .app(endpointHitEntity.getApp())
                .uri(endpointHitEntity.getUri())
                .ip(endpointHitEntity.getIp())
                .createdOn(endpointHitEntity.getCreatedOn())
                .build();
    }

}
