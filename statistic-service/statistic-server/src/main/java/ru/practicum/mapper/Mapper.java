package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.StatisticDto;
import ru.practicum.model.EndpointHit;

@UtilityClass
public class Mapper {

    public EndpointHit toEndpointHit(StatisticDto statisticDto) {
        return EndpointHit
                .builder()
                .app(statisticDto.getApp())
                .uri(statisticDto.getUri())
                .ip(statisticDto.getIp())
                .created(statisticDto.getCreated())
                .build();
    }

    public StatisticDto toStatDto(EndpointHit endpointHit) {
        return StatisticDto
                .builder()
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .created(endpointHit.getCreated())
                .build();
    }

}
