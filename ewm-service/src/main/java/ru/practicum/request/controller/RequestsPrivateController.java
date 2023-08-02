package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.defaultComponent.ewmService.dto.request.ParticipationResponseDto;
import ru.practicum.request.service.RequestPrivateService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class RequestsPrivateController {

    private final RequestPrivateService privateRequestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationResponseDto> getUserRequests(@Positive @PathVariable long userId,
                                                          @RequestParam(defaultValue = "0") int from,
                                                          @RequestParam(defaultValue = "10") int size) {
        log.info("EWM-SERVICE-private => Запрошен список запросов на участие пользователем по id => {}, " +
                "from => {}, size => {}", userId, from, size);
        return privateRequestService.getUserRequests(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationResponseDto createRequest(@Positive @PathVariable long userId,
                                                  @Positive @RequestParam long eventId) {
        log.info("EWM-SERVICE-private => Запрошено создание запроса на участие в событии по id => {}, пользователем по id => {}",
                eventId, userId);
        return privateRequestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationResponseDto cancelRequest(@Positive @PathVariable long userId,
                                                  @Positive @PathVariable long requestId) {
        log.info("EWM-SERVICE-private => Запрошено удаление запроса на участие по id => {}, пользователем по id => {}",
                requestId, userId);
        return privateRequestService.cancelRequest(userId, requestId);
    }

}
