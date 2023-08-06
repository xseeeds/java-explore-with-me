package ru.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.defaultComponent.statisticServer.dto.StatisticRequest;
import ru.defaultComponent.statisticServer.dto.ViewStatistic;
import ru.server.service.StatisticService;

import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
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

    private StatisticRequest statisticRequest;
    private ViewStatistic viewStatistic;

    @Test
    @SneakyThrows
    void addStatisticTest() {
        statisticRequest = StatisticRequest
                .builder()
                .app("test-app")
                .uri("/events")
                .eventsIds(emptyList())
                .ip("255.255.255.255")
                .createdOn(LocalDateTime.now().truncatedTo(SECONDS))
                .build();

        mockMvc.perform(post("/hit")
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(statisticRequest)))
                .andExpect(
                        status().isCreated());

        verify(statisticService, times(1))
                .addStatistic(statisticRequest);
    }

    @Test
    @SneakyThrows
    void getStatisticsTest() {
        viewStatistic = ViewStatistic
                .builder()
                .app("test-app")
                .uri("/events")
                .hits(1L)
                .build();

        when(statisticService
                .getStatistics(
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        anyList(),
                        anyBoolean()
                ))
                .thenReturn(List.of(viewStatistic));

        mockMvc.perform(get("/stats")
                        .param("start", "2020-05-05 12:12:12")
                        .param("end", "2035-05-05 12:12:12"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].app").value(viewStatistic.getApp()),
                        jsonPath("$[0].uri").value(viewStatistic.getUri()),
                        jsonPath("$[0].hits").value(viewStatistic.getHits()));
    }

}