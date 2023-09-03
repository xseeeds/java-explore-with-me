package ru.practicum.event.service;

import ru.defaultComponent.ewmServer.dto.event.EventFullResponseDto;
import ru.defaultComponent.ewmServer.enums.EventState;
import ru.defaultComponent.ewmServer.dto.event.UpdateEventAdminRequestDto;
import ru.defaultComponent.exception.exp.BadRequestException;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.event.model.EventEntity;
import java.util.List;

public interface EventAdminService {

    List<EventFullResponseDto> getAllEvents(List<Long> users,
                                            List<EventState> states,
                                            List<Long> categories,
                                            String rangeStart,
                                            String rangeEnd,
                                            int from,
                                            int size);

    EventFullResponseDto updateEvent(long eventId,
                                     UpdateEventAdminRequestDto updateEventAdminRequestDto)
            throws BadRequestException, NotFoundException, ConflictException;

    List<EventEntity> findAllByIds(List<Long> eventsIds);

    EventEntity findEventEntityById(long eventId) throws NotFoundException;

    EventEntity findEventEntityByIdAndStatusPublished(long eventId) throws NotFoundException;

    void checkEventEntityIsExistById(long eventId) throws NotFoundException;

    void saveEventEntity(EventEntity eventEntity);

    void checkEventsByCategoryId(long categoryId) throws ConflictException;

}
