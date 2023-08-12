package ru.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.defaultComponent.statisticServer.dto.StatisticRequest;
import ru.defaultComponent.statisticServer.dto.ViewStatistic;
import ru.defaultComponent.exception.exp.BadRequestException;
import ru.server.mapper.StatisticMapper;
import ru.server.dao.StatisticRepository;
import java.time.LocalDateTime;
import java.util.List;

import static ru.defaultComponent.dateTime.CheckLocalDateTime.checkStartIsAfterEndNotBeNull;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository statisticRepository;

    @Override
    @Transactional
    @Modifying
    public List<StatisticRequest> addStatistic(StatisticRequest statisticRequest) {
        final List<StatisticRequest> statisticRequestList;
        if (!statisticRequest.getEventsIds().isEmpty()) {
            statisticRequestList = StatisticMapper
                    .toStatisticRequestList(
                            statisticRepository.saveAll(
                                    StatisticMapper.toEndpointHitEntityList(statisticRequest)),
                            statisticRequest.getEventsIds());
        } else {
            statisticRequestList = StatisticMapper
                    .toStatisticRequestList(
                            statisticRepository.save(
                                    StatisticMapper.toEndpointHitEntity(statisticRequest)));
        }
        log.info("STATISTIC => Создан statisticRequestList в статистике => {}", statisticRequestList);
        return statisticRequestList;
    }

    @Override
    public List<ViewStatistic> getStatistics(LocalDateTime start,
                                             LocalDateTime end,
                                             List<String> uris,
                                             boolean unique) throws BadRequestException {
        checkStartIsAfterEndNotBeNull(start, end);
        final List<ViewStatistic> viewStatisticList;
        if (uris.isEmpty()) {
            if (!unique) {
                viewStatisticList = statisticRepository.findAllStatistics(start, end);
            } else {
                viewStatisticList = statisticRepository.findAllStatisticsForUniqueIp(start, end);
            }
        } else {
            if (!unique) {
                viewStatisticList = statisticRepository.findStatisticByUris(start, end, uris);
            } else {
                viewStatisticList = statisticRepository.findStatisticForUniqueIp(start, end, uris);
            }
        }
        log.info("STATISTIC => Получен viewStatisticList size => {}", viewStatisticList.size());
        return viewStatisticList;
    }

}
