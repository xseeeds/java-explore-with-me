package ru.practicum.event.service;

import ru.defaultComponent.ewmService.dto.event.EventFullDto;
import ru.defaultComponent.ewmService.dto.event.EventShortDto;
import ru.defaultComponent.exception.exp.BadRequestException;
import ru.defaultComponent.exception.exp.NotFoundException;

import java.util.List;

public interface EventPublicService {

    List<EventShortDto> getAllEvents(String text,
                                     List<Long> categories,
                                     Boolean paid,
                                     String rangeStart,
                                     String rangeEnd,
                                     Boolean onlyAvailable,
                                     String sort,
                                     int from,
                                     int size) throws BadRequestException;

    EventFullDto getEventById(long eventId) throws NotFoundException;

}
