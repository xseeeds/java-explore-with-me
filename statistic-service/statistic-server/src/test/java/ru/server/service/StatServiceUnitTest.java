package ru.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.defaultComponent.statisticServer.dto.ViewStatistic;
import ru.defaultComponent.statisticServer.dto.StatisticDto;
import ru.server.model.EndpointHitEntity;
import ru.server.dao.StatisticRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.defaultComponent.pageRequest.UtilPage.getPage;

@ExtendWith(MockitoExtension.class)
class StatServiceUnitTest {
    @Mock
    private StatisticRepository statisticRepository;
    @InjectMocks
    private StatisticServiceImpl statisticService;

    private StatisticDto statisticDto;
    private EndpointHitEntity endpointHitEntity;
    private EndpointHitEntity endpointHitEntityWithId;
    private ViewStatistic hitDto;
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

        statisticDto = StatisticDto
                .builder()
                .app("test-app")
                .uri("/test")
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

        hitDto = ViewStatistic
                .builder()
                .app(statisticDto.getApp())
                .uri(statisticDto.getUri())
                .hits(1L)
                .build();
    }

    @Test
    void addStatisticTest() {
        when(statisticRepository
                .save(any(EndpointHitEntity.class)))
                .thenReturn(endpointHitEntityWithId);

        statisticService.addStatistic(statisticDto);

        verify(statisticRepository, times(1))
                .save(endpointHitEntity);
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
                        new PageImpl<>(List.of(hitDto)));

        expectedList = List.of(hitDto);
        actualList = statisticService.getStatistics(
                now.minusDays(1),
                now.plusDays(1),
                List.of(),
                false,
                0,
                10);

        assertEquals(expectedList, actualList);
    }
}
