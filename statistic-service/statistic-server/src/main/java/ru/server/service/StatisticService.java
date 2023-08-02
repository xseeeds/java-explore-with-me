package ru.server.service;

import ru.defaultComponent.statisticServer.dto.ViewStatistic;
import ru.defaultComponent.statisticServer.dto.StatisticDto;
import ru.defaultComponent.exception.exp.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {

    StatisticDto addStatistic(StatisticDto statisticDto);

    List<ViewStatistic> getStatistics(LocalDateTime start,
                                      LocalDateTime end,
                                      List<String> uris,
                                      boolean unique,
                                      int from,
                                      int size) throws BadRequestException;

}
