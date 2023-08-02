package ru.practicum.request.service;

import ru.defaultComponent.ewmService.dto.event.ParticipationRequestDto;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;

import java.util.List;

public interface RequestPrivateService {

    List<ParticipationRequestDto> getUserRequests(long userId, int from, int size) throws NotFoundException;

    ParticipationRequestDto createRequest(long userId, long eventId) throws NotFoundException, ConflictException;

    ParticipationRequestDto cancelRequest(long userId, long requestId) throws NotFoundException;

}
