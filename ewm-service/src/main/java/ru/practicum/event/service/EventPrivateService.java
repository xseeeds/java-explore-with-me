package ru.practicum.event.service;

import ru.defaultComponent.ewmService.dto.event.*;
import ru.defaultComponent.ewmService.dto.event.EventRequestStatusUpdateDto;
import ru.defaultComponent.ewmService.dto.request.ParticipationResponseDto;
import ru.defaultComponent.ewmService.dto.event.UpdateEventUserRequestDto;
import ru.defaultComponent.exception.exp.BadRequestException;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;

import java.util.List;

public interface EventPrivateService {

    EventFullResponseDto addNewEvent(long userId, CreateEventRequestDto eventDto) throws BadRequestException, NotFoundException;

    List<EventShortResponseDto> getAllUserEvents(long userId, int from, int size) throws NotFoundException;

    EventFullResponseDto getEventByUser(long userId, long eventId) throws NotFoundException;

    EventFullResponseDto updateEventByUser(long userId, long eventId, UpdateEventUserRequestDto updateEventUserRequestDto)
            throws BadRequestException, NotFoundException, ConflictException;

    List<ParticipationResponseDto> getUserEventRequests(long userId, long eventId, int from, int size) throws NotFoundException;

    EventResponseStatusUpdateDto changeRequestsStatus(long userId, long eventId,
                                                      EventRequestStatusUpdateDto eventRequestStatusUpdateDto)
            throws NotFoundException, ConflictException;

}
