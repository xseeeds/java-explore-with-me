package ru.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.defaultComponent.statisticServer.dto.ViewStatistic;
import ru.defaultComponent.statisticServer.dto.StatisticDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static ru.defaultComponent.dateTime.DefaultDateTimeFormatter.getStringFormattingLocalDateTime;

@Slf4j
@Service
public class StatisticClient {

    private final WebClient client;


    public StatisticClient(
            @Value("${statistic-server.url}") String serverUrl
    ) {
        this.client = WebClient
                .builder()
                .baseUrl(serverUrl)
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public void save(StatisticDto statisticDto) {
        final StatisticDto statisticDtoResponse = client
                .post()
                .uri("/hit")
                .body(BodyInserters.fromValue(statisticDto))
                .retrieve()
                .bodyToMono(StatisticDto.class)
                .block();
        log.info("STATISTIC-CLIENT => Создан запрос сохранение статистики по посещениям StatisticDto => {}",
                statisticDtoResponse);
    }

    public List<ViewStatistic> getStatistics(LocalDateTime start,
                                             LocalDateTime end,
                                             List<String> uris,
                                             boolean unique) {
        final String startDate = getStringFormattingLocalDateTime(start);
        final String endDate = getStringFormattingLocalDateTime(end);
        final List<ViewStatistic> viewStatisticList = Objects.requireNonNull(client
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/stats")
                                .queryParam("start", startDate)
                                .queryParam("end", endDate)
                                .queryParam("uris", uris)
                                .queryParam("unique", unique ? "true" : "false")
                                .build())
                        .retrieve()
                        .onStatus(HttpStatus::is4xxClientError,
                                error -> Mono.error(new RuntimeException("API not found")))
                        .onStatus(HttpStatus::is5xxServerError,
                                error -> Mono.error(new RuntimeException("Server is not responding")))
                        .toEntityList(ViewStatistic.class)
                        .block())
                .getBody();
        log.info("STATISTIC-CLIENT => Запрошена статистика по посещениям с => {} по => {}, uris => {}, unique => {}, " +
                        "viewStatisticList.size => {}", start, end, uris, unique,
                viewStatisticList != null ? viewStatisticList.size() : "viewStatisticList is null");
        return viewStatisticList != null ? viewStatisticList : emptyList();
    }
}