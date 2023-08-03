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
import static ru.defaultComponent.pageRequest.UtilPage.getPage;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class StatisticServiceIntegrationTest {

    private final StatisticService statisticService;
    private final StatisticRepository statisticRepository;

    private StatisticRequest statisticRequest;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        statisticRequest = StatisticRequest
                .builder()
                .app("test-app")
                .uri("/test")
                .eventsIds(List.of(1L, 2L))
                .ip("255.255.255.255")
                .createdOn(now)
                .build();
    }

    @Test
    @SneakyThrows
    void addAndGetStatisticTest() {
        statisticService.addStatistic(statisticRequest);

        final List<ViewStatistic> expectedList = statisticRepository
                .findAllStatistics(
                        now.minusDays(1),
                        now.plusDays(1),
                        getPage(0, 10))
                .getContent();

        final List<ViewStatistic> actualList = statisticService
                .getStatistics(
                        now.minusDays(1),
                        now.plusDays(1),
                        List.of("/test/1", "/test/2"),
                        true,
                        0,
                        10);

        assertEquals(expectedList, actualList);
    }
}
