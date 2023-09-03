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
import ru.defaultComponent.ewmServer.dto.event.EventFullResponseDto;
import ru.defaultComponent.ewmServer.dto.event.EventShortResponseDto;
import ru.practicum.event.service.EventPublicService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventPublicController {

    final EventPublicService eventPublicService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortResponseDto> getAllEvents(@RequestParam(required = false) String text,
                                                    @RequestParam(required = false) List<Long> categories,
                                                    @RequestParam(required = false) Boolean paid,
                                                    @RequestParam(required = false) String rangeStart,
                                                    @RequestParam(required = false) String rangeEnd,
                                                    @RequestParam(required = false) Boolean onlyAvailable,
                                                    @RequestParam(required = false) String sort,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    HttpServletRequest httpServletRequest) {
        log.info("EWM-SERVER-public => Запрошен поиск событий => " +
                        "text => {}, categories => {}, paid => {}, rangeStart => {}, rangeEnd => {}, " +
                        "onlyAvailable => {}, sort => {}, from => {}, size => {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventPublicService.getAllEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                httpServletRequest);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullResponseDto getEventById(@Positive @PathVariable long eventId,
                                             HttpServletRequest httpServletRequest) {
        log.info("EWM-SERVER-public => Запрошено событие по id => {}", eventId);
        return eventPublicService.getEventById(eventId, httpServletRequest);
    }

}
