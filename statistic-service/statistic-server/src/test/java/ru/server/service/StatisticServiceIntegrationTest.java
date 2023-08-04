package ru.server.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.defaultComponent.statisticServer.dto.ViewStatistic;
import ru.defaultComponent.statisticServer.dto.StatisticRequest;
import ru.server.dao.StatisticRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class StatisticServiceIntegrationTest {

    private final StatisticService statisticService;
    private final StatisticRepository statisticRepository;

    private StatisticRequest statisticRequest;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        statisticRequest = StatisticRequest
                .builder()
                .app("test-app")
                .uri("/events")
                .eventsIds(List.of(1L, 2L))
                .ip("255.255.255.255")
                .build();
    }

    @Test
    @SneakyThrows
    void addAndGetStatisticTest() {
        now = LocalDateTime.now();
        statisticRequest.setCreatedOn(now);

        statisticService.addStatistic(statisticRequest);

        final List<ViewStatistic> expectedListNotUnique = statisticRepository
                .findStatisticByUris(
                        now.minusDays(1),
                        now.plusDays(1),
                        List.of("/events/1", "/events/2"));

        final List<ViewStatistic> expectedListUnique = statisticRepository
                .findStatisticForUniqueIp(
                        now.minusDays(1),
                        now.plusDays(1),
                        List.of("/events/1", "/events/2"));

        final List<ViewStatistic> actualListNotUnique = statisticService
                .getStatistics(
                        now.minusDays(1),
                        now.plusDays(1),
                        List.of("/events/1", "/events/2"),
                        false);

        final List<ViewStatistic> actualListUnique = statisticService
                .getStatistics(
                        now.minusDays(1),
                        now.plusDays(1),
                        List.of("/events/1", "/events/2"),
                        true);

        assertEquals(expectedListNotUnique, actualListNotUnique);
        assertEquals(expectedListUnique, actualListUnique);
    }
}
