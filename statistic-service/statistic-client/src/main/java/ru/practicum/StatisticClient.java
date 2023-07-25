package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ru.practicum.DefaultDateTimeFormatter.getDefaultDateTimeFormatter;

@Service
public class StatisticClient {

    private final WebClient client;

    @Value("${statistic-client.url:http://localhost:9090}")
    private String url;

    public StatisticClient() {
        this.client = WebClient
                .builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public StatisticDto createStatistic(StatisticDto statDto) {
        return client
                .post()
                .uri("/hit")
                .body(statDto, StatisticDto.class)
                .retrieve()
                .bodyToMono(StatisticDto.class)
                .block();
    }

    public List<HitDto> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        final String startDate = start.format(getDefaultDateTimeFormatter());
        final String endDate = end.format(getDefaultDateTimeFormatter());
        return Objects.requireNonNull(client
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/stats")
                                .queryParam("start", startDate)
                                .queryParam("end", endDate)
                                .queryParam("uris", uris)
                                .queryParam("unique", unique ? "true" : "false")
                                .build())
                        .retrieve()
                        .toEntityList(HitDto.class)
                        .block())
                .getBody();
    }
}