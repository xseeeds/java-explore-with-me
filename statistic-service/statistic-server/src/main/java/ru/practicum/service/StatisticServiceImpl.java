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

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final StatisticRepository repository;
    private final EntityManager entityManager;

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

/*
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
*/

/*
        this.checkLocalDateTime(start, end);
        final boolean emptyUris = uris.isEmpty();

        final String sqlCount = "count(" + (unique ? "distinct h.ip" : "*") + ")";

        final String sqlBaseQuery = "select new ru.practicum.HitDto(h.app, h.uri," + sqlCount + ")" +
                " from EndpointHit h " +
                " where h.created between :start and :end" + (!emptyUris ? " and h.uri in (:uris)" : "") +
                " group by app, uri" +
                " order by " + sqlCount + " desc";

        final TypedQuery<HitDto> hitStatDtoTypedQuery = entityManager.createQuery(sqlBaseQuery, HitDto.class)
                .setParameter("start", start)
                .setParameter("end", end);
        if (!emptyUris) {
            hitStatDtoTypedQuery.setParameter("uris", uris);
        }
        final List<HitDto> hitDtoList = hitStatDtoTypedQuery.getResultList();

        log.info("Получен hitDtoList size => {}", hitDtoList.size());
        return hitDtoList;
*/

        this.checkLocalDateTime(start, end);
        final List<EndpointHit> endpointHitList = repository.findAll();

/*
        if (uris.isEmpty()) {
            endpointHitList = repository.findByDatetime(start, end);
        } else {
            endpointHitList = repository.findByDatetimeAndUris(start, end, uris);
        }
*/

        final List<HitDto> hitDtoList;

        final Map<String, List<EndpointHit>> mapByUri;
        if (uris.isEmpty()) {
            mapByUri = endpointHitList
                    .stream()

                    .filter(endpointHit ->
                            !endpointHit.getCreated().isBefore(start)
                                    && !endpointHit.getCreated().isAfter(end))

                    .collect(
                            groupingBy(EndpointHit::getUri));
        } else {
            mapByUri = endpointHitList
                    .stream()

                    .filter(endpointHit ->
                            uris.stream().anyMatch(u -> endpointHit.getUri().equalsIgnoreCase(u))
                                    && !endpointHit.getCreated().isBefore(start)
                                    && !endpointHit.getCreated().isAfter(end))

                    .collect(
                            groupingBy(EndpointHit::getUri));
        }
        if (!unique) {
            hitDtoList = mapByUri
                    .keySet()
                    .stream()
                    .map(key ->
                            new HitDto(
                                    mapByUri.get(key).get(0).getApp(),
                                    key,
                                    (long) mapByUri.get(key).size()))
                    .collect(toList());
        } else {
            hitDtoList = mapByUri
                    .keySet()
                    .stream()
                    .map(key ->
                            new HitDto(
                                    mapByUri.get(key).get(0).getApp(),
                                    key,
                                    mapByUri.get(key)
                                            .stream()
                                            .map(EndpointHit::getIp)
                                            .distinct()
                                            .count()))
                    .collect(toList());
        }
        hitDtoList.sort(comparing(HitDto::getHits).reversed());

        log.info("Получен hitDtoList size => {}", hitDtoList.size());
        return hitDtoList;


    }

    private void checkLocalDateTime(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new BadRequestException("Время начала => " + start + " не может быть позже времени окончания => " + end);
        }
    }

}
