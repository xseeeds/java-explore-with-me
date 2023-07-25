package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.HitDto;
import ru.practicum.StatisticDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.storage.StatisticRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StatServiceUnitTest {
    @Mock
    private StatisticRepository statisticRepository;
    @InjectMocks
    private StatisticServiceImpl statisticService;

    private StatisticDto statisticDto;
    private EndpointHit endpointHit;
    private EndpointHit endpointHitWithId;
    private HitDto hitDto;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {

        endpointHit = EndpointHit
                .builder()
                .app("test-app")
                .uri("/test")
                .ip("255.255.255.255")
                .created(now)
                .build();

        statisticDto = StatisticDto
                .builder()
                .app("test-app")
                .uri("/test")
                .ip("255.255.255.255")
                .created(now)
                .build();

        endpointHitWithId = EndpointHit
                .builder()
                .id(1L)
                .app("test-app")
                .uri("/test")
                .ip("255.255.255.255")
                .created(now)
                .build();

        hitDto = HitDto
                .builder()
                .app(statisticDto.getApp())
                .uri(statisticDto.getUri())
                .hits(1L)
                .build();
    }

    @Test
    void addStatisticTest() {
        when(statisticRepository
                .save(any(EndpointHit.class)))
                .thenReturn(endpointHitWithId);

        statisticService.addStatistic(statisticDto);

        verify(statisticRepository, times(1))
                .save(endpointHit);
    }

    @Test
    void getStatisticsTest() {
        final List<HitDto> expectedList;
        final List<HitDto> actualList;

        when(statisticRepository.findAllStatistics(now.minusDays(1), now.plusDays(1)))
                .thenReturn(List.of(hitDto));

        expectedList = List.of(hitDto);
        actualList = statisticService.getStatistics(now.minusDays(1), now.plusDays(1), List.of(), false);

        assertEquals(expectedList, actualList);
    }
}
