package ru.practicum.request.service;

import ru.defaultComponent.ewmService.dto.request.ParticipationResponseDto;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;

import java.util.List;

public interface RequestPrivateService {

    List<ParticipationResponseDto> getUserRequests(long userId, int from, int size) throws NotFoundException;

    ParticipationResponseDto createRequest(long userId, long eventId) throws NotFoundException, ConflictException;

    ParticipationResponseDto cancelRequest(long userId, long requestId) throws NotFoundException;

}
