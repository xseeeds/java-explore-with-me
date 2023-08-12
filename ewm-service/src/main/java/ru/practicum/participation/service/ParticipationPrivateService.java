package ru.practicum.participation.service;

import ru.defaultComponent.ewmService.dto.participation.ParticipationResponseDto;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;
import java.util.List;

public interface ParticipationPrivateService {

    List<ParticipationResponseDto> getUserParticipation(long userId, int from, int size) throws NotFoundException;

    ParticipationResponseDto createParticipation(long userId, long eventId) throws NotFoundException, ConflictException;

    ParticipationResponseDto cancelParticipation(long userId, long participationId) throws NotFoundException;

}
