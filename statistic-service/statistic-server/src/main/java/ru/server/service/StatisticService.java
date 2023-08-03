package ru.server.service;

import ru.defaultComponent.statisticServer.dto.StatisticRequest;
import ru.defaultComponent.statisticServer.dto.ViewStatistic;
import ru.defaultComponent.exception.exp.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {

    List<StatisticRequest> addStatistic(StatisticRequest statisticRequest);

    List<ViewStatistic> getStatistics(LocalDateTime start,
                                      LocalDateTime end,
                                      List<String> uris,
                                      boolean unique) throws BadRequestException;

}
