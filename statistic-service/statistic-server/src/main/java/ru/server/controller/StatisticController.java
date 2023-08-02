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
import ru.defaultComponent.dateTime.DefaultDateTimeFormatter;
import ru.defaultComponent.statisticServer.dto.ViewStatistic;
import ru.defaultComponent.statisticServer.dto.StatisticDto;

import ru.server.service.StatisticService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatisticDto addStatistic(@Valid @RequestBody StatisticDto statisticDto) {
        log.info("STATISTIC-SERVER => Запрошено сохранение информации => {}", statisticDto);
        return statisticService.addStatistic(statisticDto);
    }

    @GetMapping("/stats")
    public List<ViewStatistic> getStatistics(@RequestParam @DateTimeFormat(pattern = DefaultDateTimeFormatter.PATTERN_DATE_TIME) LocalDateTime start,
                                             @RequestParam @DateTimeFormat(pattern = DefaultDateTimeFormatter.PATTERN_DATE_TIME) LocalDateTime end,
                                             @RequestParam(defaultValue = "") List<String> uris,
                                             @RequestParam(defaultValue = "false") boolean unique,
                                             @RequestParam(value = "from", defaultValue = "0") int from,
                                             @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("STATISTIC-SERVER => Запрошена статистика по посещениям с => {} по => {}, uris => {}, unique => {}, from => {}, size => {}",
                start, end, uris, unique, from, size);
        return statisticService.getStatistics(start, end, uris, unique, from, size);
    }

}
