package ru.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.defaultComponent.statisticServer.dto.ViewStatistic;
import ru.defaultComponent.statisticServer.dto.StatisticDto;
import ru.defaultComponent.exception.exp.BadRequestException;
import ru.server.mapper.StatisticMapper;
import ru.server.dao.StatisticRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.defaultComponent.pageRequest.UtilPage.getPage;
import static ru.defaultComponent.dateTime.CheckLocalDateTime.checkStartIsAfterEndPublic;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository statisticRepository;

    @Override
    @Transactional
    @Modifying
    public StatisticDto addStatistic(StatisticDto statisticDto) {
        final StatisticDto createdStatisticDto = StatisticMapper
                .toStatisticDto(
                        statisticRepository.save(
                                StatisticMapper.toEndpointHitEntity(statisticDto)));
        log.info("STATISTIC => Создан createdStatisticDto в статистике => {}", createdStatisticDto);
        return createdStatisticDto;
    }

    @Override
    public List<ViewStatistic> getStatistics(LocalDateTime start,
                                             LocalDateTime end,
                                             List<String> uris,
                                             boolean unique,
                                             int from,
                                             int size) throws BadRequestException {
        checkStartIsAfterEndPublic(start, end);
        final Page<ViewStatistic> hitDtoPage;
        if (uris.isEmpty()) {
            if (!unique) {
                hitDtoPage = statisticRepository.findAllStatistics(start, end, getPage(from, size));
            } else {
                hitDtoPage = statisticRepository.findAllStatisticsForUniqueIp(start, end, getPage(from, size));
            }
        } else {
            if (!unique) {
                hitDtoPage = statisticRepository.findStatisticByUris(start, end, uris, getPage(from, size));
            } else {
                hitDtoPage = statisticRepository.findStatisticForUniqueIp(start, end, uris, getPage(from, size));
            }
        }
        log.info("STATISTIC => Получен hitDtoPage size => {}", hitDtoPage.getTotalElements());
        return hitDtoPage.getContent();
    }

}
