package ru.practicum.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.HitDto;
import ru.practicum.StatisticDto;
import ru.practicum.service.StatisticService;

import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
                .created(LocalDateTime.now().truncatedTo(SECONDS))
                .build();

        mockMvc.perform(post("/hit")
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(statisticDto)))
                .andExpect(
                        status().isCreated());

        verify(statisticService, times(1))
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
                .created(LocalDateTime.now().truncatedTo(SECONDS))
                .build();
        hitDto = HitDto
                .builder()
                .app("test-app")
                .uri("/test")
                .hits(1L)
                .build();

        when(statisticService
                .getStatistics(
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        anyList(),
                        anyBoolean()))
                .thenReturn(List.of(hitDto));

        mockMvc.perform(get("/stats")
                        .param("start", "2020-05-05 12:12:12")
                        .param("end", "2035-05-05 12:12:12"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].app").value(hitDto.getApp()),
                        jsonPath("$[0].uri").value(hitDto.getUri()),
                        jsonPath("$[0].hits").value(hitDto.getHits()));
    }

}