package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.HitDto;
import ru.practicum.StatisticDto;
import ru.practicum.storage.StatisticRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class StatisticServiceIntegrationTest {
    private final StatisticService statisticService;
    private final StatisticRepository statisticRepository;

    private StatisticDto statisticDto;
    private LocalDateTime localDateTime;

    @BeforeEach
    void setUp() {
        localDateTime = LocalDateTime.now();

        statisticDto = StatisticDto
                .builder()
                .app("test-app")
                .uri("/test")
                .ip("255.255.255.255")
                .created(localDateTime)
                .build();
    }

    @Test
    @SneakyThrows
    void addAndGetStatisticTest() {
        final List<HitDto> actualList;

        statisticService.addStatistic(statisticDto);

        final List<HitDto> expectedList = statisticRepository
                .findAllStatistics(localDateTime.minusDays(1), localDateTime.plusDays(1));

        actualList = statisticService
                .getStatistics(
                        localDateTime.minusDays(1),
                        localDateTime.plusDays(1),
                        List.of(),
                        false);

       assertEquals(expectedList, actualList);
    }
}
