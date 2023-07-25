package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HitDto;
import ru.practicum.StatisticDto;
import ru.practicum.exception.exp.BadRequestException;
import ru.practicum.mapper.Mapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.storage.StatisticRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final StatisticRepository repository;

    @Override
    @Transactional
    public StatisticDto addStatistic(StatisticDto statisticDto) {
        final EndpointHit endpointHit = repository.save(Mapper.toEndpointHit(statisticDto));
        final StatisticDto createdStatistic = Mapper.toStatDto(repository.save(endpointHit));

        log.info("Создан endpointHit в статистике => {}", endpointHit);
        return createdStatistic;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HitDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        this.checkLocalDateTime(start, end);
        final List<HitDto> hitDtoList;

        if (uris.isEmpty()) {
            if (!unique) {
                hitDtoList = repository.findAllStatistics(start, end);
            } else {
                hitDtoList = repository.findAllStatisticsForUniqueIp(start, end);
            }
        } else {
            if (!unique) {
                hitDtoList = repository.findStatisticByUris(start, end, uris);
            } else {
                hitDtoList = repository.findStatisticForUniqueIp(start, end, uris);
            }
        }

        log.info("Получен hitDtoList size => {}", hitDtoList.size());
        return hitDtoList;
    }

    private void checkLocalDateTime(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new BadRequestException("Время начала => " + start + " не может быть позже времени окончания => " + end);
        }
    }

}
