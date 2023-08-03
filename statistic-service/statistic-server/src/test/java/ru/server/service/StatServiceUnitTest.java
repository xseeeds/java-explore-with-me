package ru.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
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
import static ru.defaultComponent.pageRequest.UtilPage.getPage;

@ExtendWith(MockitoExtension.class)
class StatServiceUnitTest {
    @Mock
    private StatisticRepository statisticRepository;
    @InjectMocks//Inject for only Impl
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
                .uri("/test")
                .ip("255.255.255.255")
                .createdOn(now)
                .build();

        statisticRequest = StatisticRequest
                .builder()
                .app("test-app")
                .uri("/test")
                .eventsIds(emptyList())
                .ip("255.255.255.255")
                .createdOn(now)
                .build();

        endpointHitEntityWithId = EndpointHitEntity
                .builder()
                .id(1L)
                .app("test-app")
                .uri("/test")
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
        when(statisticRepository
                .save(any(EndpointHitEntity.class)))
                .thenReturn(endpointHitEntityWithId);

        statisticServiceImpl.addStatistic(statisticRequest);

        verify(statisticRepository, times(1))
                .save(endpointHitEntity);
    }

    @Test
    void addStatisticSaveAllTest() {
        endpointHitEntityWithId.setUri("/test/1");
        endpointHitEntity.setUri("/test/1");
        statisticRequest.setEventsIds(List.of(1L));

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
                        now.plusDays(1),
                        getPage(0, 10)))
                .thenReturn(
                        new PageImpl<>(List.of(viewStatistic)));

        expectedList = List.of(viewStatistic);
        actualList = statisticServiceImpl.getStatistics(
                now.minusDays(1),
                now.plusDays(1),
                emptyList(),
                false,
                0,
                10);

        assertEquals(expectedList, actualList);
    }
}
