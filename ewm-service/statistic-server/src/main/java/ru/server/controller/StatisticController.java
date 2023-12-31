package ru.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.defaultComponent.statisticServer.dto.StatisticRequest;
import ru.defaultComponent.statisticServer.dto.ViewStatistic;
import ru.server.service.StatisticService;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.defaultComponent.dateTime.DefaultDateTimeFormatter.PATTERN_DATE_TIME;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public List<StatisticRequest> addStatistic(@Valid @RequestBody StatisticRequest statisticRequest) {
        log.info("STATISTIC-SERVER => Запрошено сохранение информации => {}", statisticRequest);
        return statisticService.addStatistic(statisticRequest);
    }

    @GetMapping("/stats")
    public List<ViewStatistic> getStatistics(@RequestParam(required = false)
                                             @DateTimeFormat(pattern = PATTERN_DATE_TIME) LocalDateTime start,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(pattern = PATTERN_DATE_TIME) LocalDateTime end,
                                             @RequestParam(defaultValue = "") List<String> uris,
                                             @RequestParam(defaultValue = "false") boolean unique) {
        log.info("STATISTIC-SERVER => Запрошена статистика по посещениям с => {} по => {}, uris => {}, unique => {}",
                start, end, uris, unique);
        return statisticService.getStatistics(start, end, uris, unique);
    }

}
