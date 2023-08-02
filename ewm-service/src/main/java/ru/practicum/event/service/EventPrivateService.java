package ru.practicum.event.service;

import ru.defaultComponent.ewmService.dto.event.EventFullDto;
import ru.defaultComponent.ewmService.dto.event.EventShortDto;
import ru.defaultComponent.ewmService.dto.event.NewEventDto;
import ru.defaultComponent.ewmService.dto.event.ParticipationRequestDto;
import ru.defaultComponent.ewmService.dto.event.EventRequestStatusUpdateResult;
import ru.defaultComponent.ewmService.dto.event.EventRequestStatusUpdateRequest;
import ru.defaultComponent.ewmService.dto.request.UpdateEventUserRequest;
import ru.defaultComponent.exception.exp.BadRequestException;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;

import java.util.List;

public interface EventPrivateService {

    EventFullDto addNewEvent(long userId, NewEventDto eventDto) throws BadRequestException, NotFoundException;

    List<EventShortDto> getAllUserEvents(long userId, int from, int size) throws NotFoundException;

    EventFullDto getEventByUser(long userId, long eventId) throws NotFoundException;

    EventFullDto updateEventByUser(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest)
            throws BadRequestException, NotFoundException, ConflictException;

    List<ParticipationRequestDto> getUserEventRequests(long userId, long eventId, int from, int size) throws NotFoundException;

    EventRequestStatusUpdateResult changeRequestsStatus(long userId, long eventId,
                                                        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest)
            throws NotFoundException, ConflictException;

}
