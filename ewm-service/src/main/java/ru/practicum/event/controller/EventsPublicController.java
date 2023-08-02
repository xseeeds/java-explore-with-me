package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.client.StatisticClient;
import ru.defaultComponent.ewmService.dto.event.EventFullDto;
import ru.defaultComponent.ewmService.dto.event.EventShortDto;
import ru.defaultComponent.statisticServer.dto.StatisticDto;
import ru.practicum.event.service.EventPublicService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventsPublicController {

    final EventPublicService eventPublicService;
    final StatisticClient statisticClient;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllEvents(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) List<Long> categories,
                                            @RequestParam(required = false) Boolean paid,
                                            @RequestParam(required = false) String rangeStart,
                                            @RequestParam(required = false) String rangeEnd,
                                            @RequestParam(required = false) Boolean onlyAvailable,
                                            @RequestParam(required = false) String sort,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size,
                                            HttpServletRequest httpServletRequest) {
        statisticClient.save(toStatisticDto(httpServletRequest));
        log.info("EWM-SERVICE-public => Запрошен поиск событий => " +
                        "text => {}, categories => {}, paid => {}, rangeStart => {}, rangeEnd => {}, " +
                        "onlyAvailable => {}, sort => {}, from => {}, size => {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventPublicService.getAllEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@Positive @PathVariable long eventId,
                                     HttpServletRequest httpServletRequest) {
        statisticClient.save(toStatisticDto(httpServletRequest));
        log.info("EWM-SERVICE-public => Запрошено событие по id => {}", eventId);
        return eventPublicService.getEventById(eventId);
    }

    private StatisticDto toStatisticDto(HttpServletRequest httpServletRequest) {
        return StatisticDto
                .builder()
                .app(httpServletRequest.getServerName())
                .uri(httpServletRequest.getRequestURI())
                .ip(httpServletRequest.getRemoteAddr())
                .createdOn(LocalDateTime.now())
                .build();
    }

}
