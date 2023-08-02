package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.defaultComponent.ewmService.dto.event.*;
import ru.defaultComponent.ewmService.enums.EventState;
import ru.defaultComponent.ewmService.dto.request.UpdateEventAdminRequest;
import ru.defaultComponent.ewmService.dto.request.UpdateEventUserRequest;
import ru.defaultComponent.ewmService.enums.RequestStatus;
import ru.defaultComponent.exception.exp.BadRequestException;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.category.model.CategoryEntity;
import ru.practicum.category.service.CategoryAdminService;
import ru.practicum.request.model.RequestEntity;
import ru.practicum.request.service.RequestAdminService;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.service.UserAdminService;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.EventEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static ru.defaultComponent.dateTime.DefaultDateTimeFormatter.getLocalDateTimeFormatting;
import static ru.defaultComponent.ewmService.enums.EventState.*;
import static ru.defaultComponent.ewmService.enums.StateAdminRequest.*;
import static ru.defaultComponent.ewmService.enums.StateUserRequest.*;
import static ru.defaultComponent.ewmService.enums.RequestStatus.CONFIRMED;
import static ru.defaultComponent.ewmService.enums.RequestStatus.REJECTED;
import static ru.defaultComponent.pageRequest.UtilPage.getPageSortDescByProperties;
import static ru.defaultComponent.dateTime.CheckLocalDateTime.checkEventDateToUpdateEventAdmin;
import static ru.defaultComponent.dateTime.CheckLocalDateTime.checkEventDateToAddEventPrivate;
import static ru.defaultComponent.dateTime.CheckLocalDateTime.checkEventDateToUpdateEventPrivate;
import static ru.defaultComponent.dateTime.CheckLocalDateTime.checkStartIsAfterEndPublic;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventAdminService, EventPrivateService, EventPublicService {

    private final EventRepository eventRepository;
    private final UserAdminService userAdminService;
    private final CategoryAdminService categoryAdminService;
    private final RequestAdminService requestAdminService;

    @Override
    public List<EventFullDto> getAllEvents(List<Long> users, List<EventState> states,
                                           List<Long> categories, String rangeStart,
                                           String rangeEnd, int from, int size) {
        final Page<EventFullDto> eventFullDtoPage = eventRepository.findByAdmin(users, categories, states,
                        getLocalDateTimeFormatting(rangeStart),
                        getLocalDateTimeFormatting(rangeEnd),
                        getPageSortDescByProperties(from, size, "eventDate"))
                .map(EventMapper::toEventFullDto);
        log.info("ADMIN => Поиск событий => totalElement => {} users => {}, states => {}, categories => {}, " +
                        "rangeStart => {}, rangeEnd => {}, from => {}, size => {}",
                eventFullDtoPage.getTotalElements(), users, states, categories, rangeStart, rangeEnd, from, size);
        return eventFullDtoPage.getContent();
    }

    @Transactional
    @Modifying
    @Override
    public EventFullDto updateEvent(long eventId, UpdateEventAdminRequest updateEventAdminRequest)
            throws BadRequestException, NotFoundException, ConflictException {
        final EventEntity eventEntity = this
                .findEventEntityById(eventId);
        if (updateEventAdminRequest.getStateAction() != null) {
            if (eventEntity.getState() != PENDING && updateEventAdminRequest.getStateAction() == PUBLISH_EVENT) {
                throw new ConflictException("ADMIN => Событие != PENDING");
            }
            if (eventEntity.getState() == PUBLISHED) {
                throw new ConflictException("ADMIN => Событие => PUBLISHED");
            }
            if (updateEventAdminRequest.getStateAction() == REJECT_EVENT) {
                eventEntity.setState(CANCELED);
            }
            if (updateEventAdminRequest.getStateAction() == PUBLISH_EVENT) {
                eventEntity.setState(PUBLISHED);
            }
        }
        this.setEventFields(eventEntity,
                updateEventAdminRequest.getAnnotation(),
                updateEventAdminRequest.getCategory(),
                updateEventAdminRequest.getDescription(),
                updateEventAdminRequest.getEventDate(),
                true,
                updateEventAdminRequest.getLocation(),
                updateEventAdminRequest.getPaid(),
                updateEventAdminRequest.getParticipantLimit(),
                updateEventAdminRequest.getRequestModeration(),
                updateEventAdminRequest.getTitle());
        final EventFullDto eventFullDto = EventMapper
                .toEventFullDto(
                        eventRepository.save(eventEntity));
        log.info("ADMIN => Событие обновлено по id => {}", eventId);
        return eventFullDto;
    }

    @Override
    public List<EventEntity> findAllByIds(List<Long> eventsIds) {
        final List<EventEntity> eventEntityList = eventRepository.findAllById(eventsIds);
        log.info("ADMIN => поиск событий по ids => {}, найдено size => {}", eventsIds, eventEntityList.size());
        return eventEntityList;
    }

    @Override
    public EventEntity findEventEntityById(long eventId) throws NotFoundException {
        log.info("ADMIN => запрос события по id => {} для СЕРВИСОВ", eventId);
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        "ADMIN => Событие по id => " + eventId + " не существует поиск СЕРВИСОВ"));
    }

    @Override
    public void checkEventIsExistById(long eventId) throws NotFoundException {
        log.info("ADMIN => Запрос существует событие по id => {} для СЕРВИСОВ", eventId);
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("ADMIN => Событие по id => " + eventId + " не существует");
        }
    }

    @Transactional
    @Modifying
    @Override
    public void saveEventEntity(EventEntity eventEntity) {
        eventRepository.save(eventEntity);
        log.info("ADMIN => Запрос сохранения события по id => {} для СЕРВИСОВ", eventEntity.getId());
    }

    @Transactional
    @Modifying
    @Override
    public EventFullDto addNewEvent(long userId, NewEventDto newEventDto) throws BadRequestException, NotFoundException {
        final UserEntity userEntity = userAdminService.findUserEntityById(userId);
        final CategoryEntity categoryEntity = categoryAdminService.findCategoryEntityById(newEventDto.getCategory());
        final EventEntity eventEntity = EventMapper
                .toEventEntity(
                        newEventDto, userEntity, categoryEntity);
        checkEventDateToAddEventPrivate(eventEntity.getEventDate());
        final EventFullDto eventFullDto = EventMapper
                .toEventFullDto(
                        eventRepository.save(eventEntity));
        log.info("PRIVATE => Создание нового события => {}, пользователем по id => {}", eventFullDto, userId);
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getAllUserEvents(long userId, int from, int size) throws NotFoundException {
        userAdminService.checkUserIsExistById(userId);
        final Page<EventShortDto> eventShortDtoPage = eventRepository
                .findAllByInitiator(
                        userId, getPageSortDescByProperties(from, size, "eventDate"))
                .map(EventMapper::toEventShortDto);
        log.info("PRIVATE => Список событий пользователя по id => {}, size => {}",
                userId, eventShortDtoPage.getTotalElements());
        return eventShortDtoPage.getContent();
    }

    @Override
    public EventFullDto getEventByUser(long userId, long eventId) throws NotFoundException {
        userAdminService.checkUserIsExistById(userId);
        final EventFullDto eventFullDto = EventMapper
                .toEventFullDto(
                        this.findEventEntityById(eventId));
        log.info("PRIVATE => Информация о событии по id => {}, получена пользователем по id => {}", eventId, userId);
        return eventFullDto;
    }

    @Transactional
    @Modifying
    @Override
    public EventFullDto updateEventByUser(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest)
            throws BadRequestException, NotFoundException, ConflictException {
        userAdminService.checkUserIsExistById(userId);
        final EventEntity eventEntity = this.findEventEntityById(eventId);
        if (eventEntity.getState() == PUBLISHED) {
            throw new ConflictException("PRIVATE => Событие => PUBLISHED");
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction() == CANCEL_REVIEW) {
                eventEntity.setState(CANCELED);
            }
            if (updateEventUserRequest.getStateAction() == SEND_TO_REVIEW) {
                eventEntity.setState(PENDING);
            }
        }
        this.setEventFields(eventEntity,
                updateEventUserRequest.getAnnotation(),
                updateEventUserRequest.getCategory(),
                updateEventUserRequest.getDescription(),
                updateEventUserRequest.getEventDate(),
                false,
                updateEventUserRequest.getLocation(),
                updateEventUserRequest.getPaid(),
                updateEventUserRequest.getParticipantLimit(),
                updateEventUserRequest.getRequestModeration(),
                updateEventUserRequest.getTitle());
        final EventFullDto eventFullDto = EventMapper
                .toEventFullDto(
                        eventRepository.save(eventEntity));
        log.info("PRIVATE => Событие обновлено по id => {}, пользователем по id => {}", eventId, userId);
        return eventFullDto;
    }

    @Override
    public List<ParticipationRequestDto> getUserEventRequests(long userId, long eventId, int from, int size) throws NotFoundException {
        userAdminService.checkUserIsExistById(userId);
        this.checkEventIsExistById(eventId);
        final Page<ParticipationRequestDto> participationRequestDtoList = requestAdminService
                .findAllByEventId(
                        eventId, getPageSortDescByProperties(from, size, "createdOn"))
                .map(RequestMapper::toRequestDto);
        log.info("PRIVATE => Список заявок size => {}, на участие в событии по id => {}, пользователем по id => {} получен",
                participationRequestDtoList.getTotalElements(), eventId, userId);
        return participationRequestDtoList.getContent();
    }

    @Transactional
    @Modifying
    @Override
    public EventRequestStatusUpdateResult changeRequestsStatus(long userId, long eventId,
                                                               EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest)
            throws NotFoundException, ConflictException {
        userAdminService.checkUserIsExistById(userId);
        final EventEntity eventEntity = this.findEventEntityById(eventId);
        if (eventEntity.getConfirmedRequests() >= eventEntity.getParticipantLimit()) {
            throw new ConflictException("PRIVATE => Достигнут лимит заявок на участие в событии");
        }
        final Map<RequestStatus, List<RequestEntity>> requestEntityMap = new HashMap<>();
        final List<RequestEntity> requestEntityList = requestAdminService
                .findAllById(
                        eventRequestStatusUpdateRequest.getRequestIds());
        for (RequestEntity requestEntity : requestEntityList) {
            if (requestEntity.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("PRIVATE => Статус != PENDING");
            }
            if (eventEntity.getParticipantLimit() == 0
                || (eventEntity.getParticipantLimit() > eventEntity.getConfirmedRequests() && !eventEntity.getRequestModeration())
                || (eventEntity.getParticipantLimit() > eventEntity.getConfirmedRequests() && eventRequestStatusUpdateRequest.getStatus() == RequestStatus.CONFIRMED)
            ) {
                requestEntity.setStatus(RequestStatus.CONFIRMED);
                eventEntity.setConfirmedRequests(eventEntity.getConfirmedRequests() + 1);
            } else {
                requestEntity.setStatus(RequestStatus.REJECTED);
            }
            requestEntityMap.computeIfAbsent(requestEntity.getStatus(),
                    v -> new ArrayList<>()).add(requestEntity);
        }
        requestAdminService.saveAllRequestEntity(requestEntityList);
        eventRepository.save(eventEntity);
        final EventRequestStatusUpdateResult eventRequestStatusUpdateResult = EventRequestStatusUpdateResult
                .builder()
                .confirmedRequests(requestEntityMap.getOrDefault(CONFIRMED, List.of())
                        .stream()
                        .map(RequestMapper::toRequestDto)
                        .collect(toList()))
                .rejectedRequests(requestEntityMap.getOrDefault(REJECTED, List.of())
                        .stream()
                        .map(RequestMapper::toRequestDto)
                        .collect(toList()))
                .build();
        log.info("PRIVATE => Изменение статуса события по id => {} пользователем по id => {}", eventId, userId);
        return eventRequestStatusUpdateResult;
    }

    @Override
    public List<EventShortDto> getAllEvents(String text, List<Long> categories, Boolean paid,
                                            String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                            String sort, int from, int size) throws BadRequestException {
        final LocalDateTime start = getLocalDateTimeFormatting(rangeStart);
        final LocalDateTime end = getLocalDateTimeFormatting(rangeEnd);
        checkStartIsAfterEndPublic(start, end);
        PageRequest pageRequest = null;
        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                pageRequest = getPageSortDescByProperties(from, size, "eventDate");
            } else if (sort.equals("VIEWS")) {
                pageRequest = getPageSortDescByProperties(from, size, "views");
            }
        } else {
            pageRequest = getPageSortDescByProperties(from, size, "id");
        }
        final Page<EventShortDto> eventShortDtoPage = eventRepository
                .findByPublic(categories, paid, start, end, onlyAvailable, text, pageRequest)
                .map(EventMapper::toEventShortDto);
        log.info("PUBLIC => Поиск событий => totalElement => {} text => {}, categories => {}, paid => {}, " +
                        "rangeStart => {}, rangeEnd => {}, onlyAvailable => {}, sort => {}, from => {}, size => {}",
                eventShortDtoPage.getTotalElements(), text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventShortDtoPage.getContent();
    }

    @Override
    public EventFullDto getEventById(long eventId) throws NotFoundException {
        final EventEntity eventEntity = this.findEventEntityById(eventId);
        if (eventEntity.getState() != PUBLISHED) {
            throw new NotFoundException("PUBLIC => Событие по id => " + eventId + " != PUBLISHED");
        }
        eventEntity.setViews(eventEntity.getViews() + 1);
        final EventFullDto eventFullDto = EventMapper
                .toEventFullDto(
                        eventRepository.save(eventEntity));
        log.info("PUBLIC => Событие по id => {} получено", eventId);
        return eventFullDto;
    }

    private void setEventFields(EventEntity eventEntity,
                                String annotation,
                                Long category,
                                String description,
                                LocalDateTime eventDate,
                                boolean adminOrUser,
                                LocationDto location,
                                Boolean paid,
                                Integer participantLimit,
                                Boolean requestModeration,
                                String title) {
        if (annotation != null) {
            eventEntity.setAnnotation(annotation);
        }
        if (category != null) {
            final CategoryEntity categoryEntity = categoryAdminService
                    .findCategoryEntityById(category);
            eventEntity.setCategory(category);
            eventEntity.setCategoryEntity(categoryEntity);
        }
        if (description != null) {
            eventEntity.setDescription(description);
        }
        if (eventDate != null) {
            if (adminOrUser) {
                checkEventDateToUpdateEventAdmin(eventDate);
            } else {
                checkEventDateToUpdateEventPrivate(eventDate);
            }
            eventEntity.setEventDate(eventDate);
        }
        if (location != null) {
            eventEntity.setLocation(EventMapper.toLocation(location));
        }
        if (paid != null) {
            eventEntity.setPaid(paid);
        }
        if (participantLimit != null) {
            eventEntity.setParticipantLimit(participantLimit);
        }
        if (requestModeration != null && adminOrUser) {
            eventEntity.setRequestModeration(requestModeration);
        }
        if (title != null) {
            eventEntity.setTitle(title);
        }
    }

}
