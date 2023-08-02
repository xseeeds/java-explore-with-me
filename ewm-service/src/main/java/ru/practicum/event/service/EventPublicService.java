package ru.practicum.event.service;

import ru.defaultComponent.ewmService.dto.event.EventFullResponseDto;
import ru.defaultComponent.ewmService.dto.event.EventShortResponseDto;
import ru.defaultComponent.exception.exp.BadRequestException;
import ru.defaultComponent.exception.exp.NotFoundException;

import java.util.List;

public interface EventPublicService {

    List<EventShortResponseDto> getAllEvents(String text,
                                             List<Long> categories,
                                             Boolean paid,
                                             String rangeStart,
                                             String rangeEnd,
                                             Boolean onlyAvailable,
                                             String sort,
                                             int from,
                                             int size) throws BadRequestException;

    EventFullResponseDto getEventById(long eventId) throws NotFoundException;

}
