package ru.practicum.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.HitDto;
import ru.practicum.StatisticDto;
import ru.practicum.service.StatisticService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatisticController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class StatisticControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mockMvc;

    @MockBean
    private StatisticService statisticService;

    private StatisticDto statisticDto;
    private HitDto hitDto;

    @Test
    @SneakyThrows
    void addStatisticTest() {
        statisticDto = StatisticDto
                .builder()
                .app("test-app")
                .uri("/test")
                .ip("255.255.255.255")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();

        mockMvc.perform(post("/hit")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(statisticDto)))
                .andExpect(
                        status().isCreated());

        Mockito.verify(statisticService, Mockito.times(1))
                .addStatistic(statisticDto);
    }

    @Test
    @SneakyThrows
    void getStatisticsTest() {
        statisticDto = StatisticDto
                .builder()
                .app("test-app")
                .uri("/test")
                .ip("255.255.255.255")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        hitDto = HitDto
                .builder()
                .app("test-app")
                .uri("/test")
                .hits(1L)
                .build();

        Mockito
                .when(statisticService
                        .getStatistics(
                                any(LocalDateTime.class),
                                any(LocalDateTime.class),
                                anyList(),
                                anyBoolean()))
                .thenReturn(List.of(hitDto));

        mockMvc.perform(get("/stats")
                        .param("start", "2000-01-01 12:12:12")
                        .param("end", "2050-01-01 12:12:12"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].app").value(hitDto.getApp()),
                        jsonPath("$[0].uri").value(hitDto.getUri()),
                        jsonPath("$[0].hits").value(hitDto.getHits()));
    }
}