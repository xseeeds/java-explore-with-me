package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.defaultComponent.ewmService.dto.event.EventShortResponseDto;
import ru.defaultComponent.ewmService.dto.event.CreateEventRequestDto;
import ru.defaultComponent.ewmService.dto.event.EventFullResponseDto;
import ru.defaultComponent.ewmService.dto.event.EventRequestStatusUpdateDto;
import ru.defaultComponent.ewmService.dto.event.EventResponseStatusUpdateDto;
import ru.defaultComponent.ewmService.dto.event.UpdateEventUserRequestDto;
import ru.defaultComponent.ewmService.dto.request.ParticipationResponseDto;
import ru.practicum.event.service.EventPrivateService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventsPrivateController {

    private final EventPrivateService eventPrivateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullResponseDto addNewEvent(@Positive @PathVariable long userId,
                                            @Valid @RequestBody CreateEventRequestDto createEventRequestDto) {
        log.info("EWM-SERVICE-private => Запрошено добавление нового события => {}, пользователем по id => {}",
                createEventRequestDto, userId);
        return eventPrivateService.addNewEvent(userId, createEventRequestDto);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortResponseDto> getAllUserEvents(@Positive @PathVariable long userId,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size) {
        log.info("EWM-SERVICE-private => Запрошен список событий пользователя по id => {}, from => {}, size => {}",
                userId, from, size);
        return eventPrivateService.getAllUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullResponseDto getEventByUser(@Positive @PathVariable long userId,
                                               @Positive @PathVariable long eventId) {
        log.info("EWM-SERVICE-private => Запрошено получение события по id => {}, пользователем по id => {}",
                eventId, userId);
        return eventPrivateService.getEventByUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullResponseDto updateEventByUser(@Positive @PathVariable long userId,
                                                  @Positive @PathVariable long eventId,
                                                  @Valid @RequestBody UpdateEventUserRequestDto updateEventUserRequestDto) {
        log.info("EWM-SERVICE-private => Запрошено обновление события по id => {}, пользователем по id => {}",
                eventId, userId);
        return eventPrivateService.updateEventByUser(userId, eventId, updateEventUserRequestDto);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationResponseDto> getUserEventRequests(@Positive @PathVariable long userId,
                                                               @Positive @PathVariable long eventId,
                                                               @RequestParam(defaultValue = "0") int from,
                                                               @RequestParam(defaultValue = "10") int size) {
        log.info("EWM-SERVICE-private => Запрошено получение списка заявок на события по id => {}, " +
                "пользователем по id => {}, from => {}, size => {}", eventId, userId, from, size);
        return eventPrivateService.getUserEventRequests(userId, eventId, from, size);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseStatusUpdateDto changeRequestsStatus(@Positive @PathVariable long userId,
                                                             @Positive @PathVariable long eventId,
                                                             @RequestBody EventRequestStatusUpdateDto eventRequestStatusUpdateDto) {
        log.info("EWM-SERVICE-private => Запрошено изменение статуса события по id => {}, пользователем по id => {}, " +
                "EventRequestStatusUpdateDto => {}", eventId, userId, eventRequestStatusUpdateDto);
        return eventPrivateService.changeRequestsStatus(userId, eventId, eventRequestStatusUpdateDto);
    }

}
