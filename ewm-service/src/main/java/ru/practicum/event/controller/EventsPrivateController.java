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
import ru.defaultComponent.ewmService.dto.event.EventRequestStatusUpdateResult;
import ru.defaultComponent.ewmService.dto.event.EventRequestStatusUpdateRequest;
import ru.defaultComponent.ewmService.dto.event.EventFullDto;
import ru.defaultComponent.ewmService.dto.event.EventShortDto;
import ru.defaultComponent.ewmService.dto.event.NewEventDto;
import ru.defaultComponent.ewmService.dto.event.ParticipationRequestDto;
import ru.practicum.event.service.EventPrivateService;
import ru.defaultComponent.ewmService.dto.request.UpdateEventUserRequest;

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
    public EventFullDto addNewEvent(@Positive @PathVariable long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("EWM-SERVICE-private => Запрошено добавление нового события => {}, пользователем по id => {}",
                newEventDto, userId);
        return eventPrivateService.addNewEvent(userId, newEventDto);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllUserEvents(@Positive @PathVariable long userId,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        log.info("EWM-SERVICE-private => Запрошен список событий пользователя по id => {}, from => {}, size => {}",
                userId, from, size);
        return eventPrivateService.getAllUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByUser(@Positive @PathVariable long userId,
                                       @Positive @PathVariable long eventId) {
        log.info("EWM-SERVICE-private => Запрошено получение события по id => {}, пользователем по id => {}",
                eventId, userId);
        return eventPrivateService.getEventByUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByUser(@Positive @PathVariable long userId,
                                          @Positive @PathVariable long eventId,
                                          @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("EWM-SERVICE-private => Запрошено обновление события по id => {}, пользователем по id => {}",
                eventId, userId);
        return eventPrivateService.updateEventByUser(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getUserEventRequests(@Positive @PathVariable long userId,
                                                              @Positive @PathVariable long eventId,
                                                              @RequestParam(defaultValue = "0") int from,
                                                              @RequestParam(defaultValue = "10") int size) {
        log.info("EWM-SERVICE-private => Запрошено получение списка заявок на события по id => {}, " +
                "пользователем по id => {}, from => {}, size => {}", eventId, userId, from, size);
        return eventPrivateService.getUserEventRequests(userId, eventId, from, size);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult changeRequestsStatus(@Positive @PathVariable long userId,
                                                               @Positive @PathVariable long eventId,
                                                               @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("EWM-SERVICE-private => Запрошено изменение статуса события по id => {}, пользователем по id => {}, " +
                "EventRequestStatusUpdateRequest => {}", eventId, userId, eventRequestStatusUpdateRequest);
        return eventPrivateService.changeRequestsStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }

}
