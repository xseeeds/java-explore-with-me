package ru.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.defaultComponent.statisticServer.dto.ViewStatistic;
import ru.defaultComponent.statisticServer.dto.StatisticRequest;
import ru.server.model.EndpointHitEntity;
import ru.server.dao.StatisticRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StatisticServiceUnitTest {
    @Mock
    private StatisticRepository statisticRepository;
//  Inject for only Impl
    @InjectMocks
    private StatisticServiceImpl statisticServiceImpl;

    private StatisticRequest statisticRequest;
    private EndpointHitEntity endpointHitEntity;
    private EndpointHitEntity endpointHitEntityWithId;
    private ViewStatistic viewStatistic;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        endpointHitEntity = EndpointHitEntity
                .builder()
                .app("test-app")
                .uri("/events/1")
                .eventId(1L)
                .ip("255.255.255.255")
                .createdOn(now)
                .build();

        statisticRequest = StatisticRequest
                .builder()
                .app("test-app")
                .uri("/events")
                .eventsIds(List.of(1L))
                .ip("255.255.255.255")
                .createdOn(now)
                .build();

        endpointHitEntityWithId = EndpointHitEntity
                .builder()
                .id(1L)
                .app("test-app")
                .uri("/events/1")
                .eventId(1L)
                .ip("255.255.255.255")
                .createdOn(now)
                .build();

        viewStatistic = ViewStatistic
                .builder()
                .app(statisticRequest.getApp())
                .uri(statisticRequest.getUri())
                .hits(1L)
                .build();
    }


    @Test
    void addStatisticTest() {
        statisticRequest.setUri("/events/1");
        statisticRequest.setEventsIds(emptyList());

        when(statisticRepository
                .save(any(EndpointHitEntity.class)))
                .thenReturn(endpointHitEntityWithId);

        statisticServiceImpl.addStatistic(statisticRequest);

        verify(statisticRepository, times(1))
                .save(endpointHitEntity);
    }


    @Test
    void addStatisticSaveAllTest() {
        endpointHitEntity.setUri("/events");
        endpointHitEntityWithId.setUri("/events");

        when(statisticRepository
                .saveAll(anyList()))
                .thenReturn(List.of(endpointHitEntityWithId));

        statisticServiceImpl.addStatistic(statisticRequest);

        verify(statisticRepository, times(1))
                .saveAll(List.of(endpointHitEntity));
    }

    @Test
    void getStatisticsTest() {
        final List<ViewStatistic> expectedList;
        final List<ViewStatistic> actualList;

        when(statisticRepository
                .findAllStatistics(
                        now.minusDays(1),
                        now.plusDays(1)))
                .thenReturn(List.of(viewStatistic));

        expectedList = List.of(viewStatistic);
        actualList = statisticServiceImpl.getStatistics(
                now.minusDays(1),
                now.plusDays(1),
                emptyList(),
                false);

        assertEquals(expectedList, actualList);
    }
}
