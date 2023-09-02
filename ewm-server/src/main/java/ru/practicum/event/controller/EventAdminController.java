package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.defaultComponent.ewmService.dto.event.EventFullResponseDto;
import ru.defaultComponent.ewmService.enums.EventState;
import ru.practicum.event.service.EventAdminService;
import ru.defaultComponent.ewmService.dto.event.UpdateEventAdminRequestDto;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventAdminService eventAdminService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullResponseDto> getAllEvents(@RequestParam(required = false) List<Long> users,
                                                   @RequestParam(required = false) List<EventState> states,
                                                   @RequestParam(required = false) List<Long> categories,
                                                   @RequestParam(required = false) String rangeStart,
                                                   @RequestParam(required = false) String rangeEnd,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        log.info("EWM-SERVER-admin => Запрошен поиск событий => " +
                        "users => {}, states => {}, categories => {}, rangeStart => {}, rangeEnd => {}, from => {}, size => {}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventAdminService.getAllEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullResponseDto updateEvent(@Positive @PathVariable long eventId,
                                            @Valid @RequestBody UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        log.info("EWM-SERVER-admin => Запрошено обновление событий по id => {}", eventId);
        return eventAdminService.updateEvent(eventId, updateEventAdminRequestDto);
    }

}
