package ru.practicum.controller;

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
import ru.practicum.HitDto;
import ru.practicum.StatisticDto;

import ru.practicum.service.StatisticService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.DefaultDateTimeFormatter.PATTERN_DATE_TIME;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatisticDto addStatistic(@RequestBody @Valid StatisticDto statisticDto) {
        log.info("STATISTIC-SERVER => Запрошено сохранение информации о : {}", statisticDto);
        return service.addStatistic(statisticDto);
    }

    @GetMapping("/stats")
    public List<HitDto> getStatistics(@RequestParam @DateTimeFormat(pattern = PATTERN_DATE_TIME) LocalDateTime start,
                                      @RequestParam @DateTimeFormat(pattern = PATTERN_DATE_TIME) LocalDateTime end,
                                      @RequestParam(defaultValue = "") List<String> uris,
                                      @RequestParam(defaultValue = "false") boolean unique) {
        log.info("STATISTIC-SERVER => Запрошена статистика по посещениям с {} по {}", start, end);
        return service.getStatistics(start, end, uris, unique);
    }

}
