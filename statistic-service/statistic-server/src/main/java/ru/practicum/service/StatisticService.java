package ru.practicum.service;

import ru.practicum.HitDto;
import ru.practicum.StatisticDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {

    StatisticDto addStatistic(StatisticDto statisticDto);

    List<HitDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

}
