package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.client.StatisticClient;
import ru.defaultComponent.ewmService.dto.event.EventShortResponseDto;
import ru.defaultComponent.ewmService.dto.event.CreateEventRequestDto;
import ru.defaultComponent.ewmService.dto.event.EventFullResponseDto;
import ru.defaultComponent.ewmService.dto.event.EventRequestStatusUpdateDto;
import ru.defaultComponent.ewmService.dto.event.EventResponseStatusUpdateDto;
import ru.defaultComponent.ewmService.dto.event.UpdateEventUserRequestDto;
import ru.defaultComponent.ewmService.dto.request.ParticipationResponseDto;
import ru.defaultComponent.ewmService.enums.EventState;
import ru.defaultComponent.ewmService.dto.event.UpdateEventAdminRequestDto;
import ru.defaultComponent.ewmService.dto.event.LocationRequestDto;
import ru.defaultComponent.ewmService.enums.RequestStatus;
import ru.defaultComponent.exception.exp.BadRequestException;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.defaultComponent.statisticServer.dto.ViewStatistic;
import ru.practicum.category.model.CategoryEntity;
import ru.practicum.category.service.CategoryAdminService;
import ru.practicum.request.model.ParticipationEntity;
import ru.practicum.request.service.RequestAdminService;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.service.UserAdminService;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.EventEntity;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
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
import static ru.defaultComponent.dateTime.CheckLocalDateTime.checkStartIsAfterEndEvent;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventAdminService, EventPrivateService, EventPublicService {

    private final EventRepository eventRepository;
    private final UserAdminService userAdminService;
    private final CategoryAdminService categoryAdminService;
    private final RequestAdminService requestAdminService;
    final StatisticClient statisticClient;

    @Override
    public List<EventFullResponseDto> getAllEvents(List<Long> users, List<EventState> states,
                                                   List<Long> categories, String rangeStart,
                                                   String rangeEnd, int from, int size) {
        final Page<EventFullResponseDto> eventFullDtoPage = eventRepository.findByAdmin(users, categories, states,
                        getLocalDateTimeFormatting(rangeStart),
                        getLocalDateTimeFormatting(rangeEnd),
                        getPageSortDescByProperties(from, size, "eventDate"))
                .map(EventMapper::toEventFullResponseDto);
        log.info("ADMIN => Поиск событий => totalElement => {} users => {}, states => {}, categories => {}, " +
                        "rangeStart => {}, rangeEnd => {}, from => {}, size => {}",
                eventFullDtoPage.getTotalElements(), users, states, categories, rangeStart, rangeEnd, from, size);
        return eventFullDtoPage.getContent();
    }

    @Transactional
    @Modifying
    @Override
    public EventFullResponseDto updateEvent(long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto)
            throws BadRequestException, NotFoundException, ConflictException {
        final EventEntity eventEntity = this.findEventEntityById(eventId);
        if (updateEventAdminRequestDto.getStateAction() != null) {
            if (eventEntity.getState() != PENDING && updateEventAdminRequestDto.getStateAction() == PUBLISH_EVENT) {
                throw new ConflictException("ADMIN => Событие != PENDING");
            }
            if (eventEntity.getState() == PUBLISHED) {
                throw new ConflictException("ADMIN => Событие => PUBLISHED");
            }
            if (updateEventAdminRequestDto.getStateAction() == REJECT_EVENT) {
                eventEntity.setState(CANCELED);
            }
            if (updateEventAdminRequestDto.getStateAction() == PUBLISH_EVENT) {
                eventEntity.setPublishedOn(LocalDateTime.now());
                eventEntity.setState(PUBLISHED);
            }
        }
        this.setEventFields(eventEntity,
                updateEventAdminRequestDto.getAnnotation(),
                updateEventAdminRequestDto.getCategory(),
                updateEventAdminRequestDto.getDescription(),
                updateEventAdminRequestDto.getEventDate(),
                true,
                updateEventAdminRequestDto.getLocation(),
                updateEventAdminRequestDto.getPaid(),
                updateEventAdminRequestDto.getParticipantLimit(),
                updateEventAdminRequestDto.getRequestModeration(),
                updateEventAdminRequestDto.getTitle());
        final EventFullResponseDto eventFullResponseDto = EventMapper
                .toEventFullResponseDto(
                        eventRepository.save(eventEntity));
        log.info("ADMIN => Событие обновлено по id => {}", eventId);
        return eventFullResponseDto;
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
    public EventEntity findEventEntityByIdAndStatusPublished(long eventId) throws NotFoundException {
        log.info("ADMIN => запрос события со статусом PUBLISHED по id => {} для СЕРВИСОВ", eventId);
        return eventRepository.findByIdAndState(eventId, PUBLISHED)
                .orElseThrow(() -> new NotFoundException(
                        "PUBLIC => Событие по id => " + eventId + " != PUBLISHED"));
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
    public EventFullResponseDto addNewEvent(long userId, CreateEventRequestDto createEventRequestDto) throws BadRequestException, NotFoundException {
        final UserEntity userEntity = userAdminService.findUserEntityById(userId);
        final CategoryEntity categoryEntity = categoryAdminService.findCategoryEntityById(createEventRequestDto.getCategory());
        final EventEntity eventEntity = EventMapper
                .toNewEventEntity(
                        createEventRequestDto, userEntity, categoryEntity);
        checkEventDateToAddEventPrivate(eventEntity.getEventDate());
        final EventFullResponseDto eventFullResponseDto = EventMapper
                .toEventFullResponseDto(
                        eventRepository.save(eventEntity));
        log.info("PRIVATE => Создание нового события => {}, пользователем по id => {}", eventFullResponseDto, userId);
        return eventFullResponseDto;
    }

    @Override
    public List<EventShortResponseDto> getAllUserEvents(long userId, int from, int size) throws NotFoundException {
        userAdminService.checkUserIsExistById(userId);
        final Page<EventShortResponseDto> eventShortDtoPage = eventRepository
                .findAllByInitiator(
                        userId, getPageSortDescByProperties(from, size, "eventDate"))
                .map(EventMapper::toEventShortResponseDto);
        log.info("PRIVATE => Список событий пользователя по id => {}, size => {}",
                userId, eventShortDtoPage.getTotalElements());
        return eventShortDtoPage.getContent();
    }

    @Override
    public EventFullResponseDto getEventByUser(long userId, long eventId) throws NotFoundException {
        userAdminService.checkUserIsExistById(userId);
        final EventFullResponseDto eventFullResponseDto = EventMapper
                .toEventFullResponseDto(
                        this.findEventEntityById(eventId));
        log.info("PRIVATE => Информация о событии по id => {}, получена пользователем по id => {}", eventId, userId);
        return eventFullResponseDto;
    }

    @Transactional
    @Modifying
    @Override
    public EventFullResponseDto updateEventByUser(long userId, long eventId, UpdateEventUserRequestDto updateEventUserRequestDto)
            throws BadRequestException, NotFoundException, ConflictException {
        userAdminService.checkUserIsExistById(userId);
        final EventEntity eventEntity = this.findEventEntityById(eventId);
        if (eventEntity.getState() == PUBLISHED) {
            throw new ConflictException("PRIVATE => Событие => PUBLISHED");
        }
        if (updateEventUserRequestDto.getStateAction() != null) {
            if (updateEventUserRequestDto.getStateAction() == CANCEL_REVIEW) {
                eventEntity.setState(CANCELED);
            }
            if (updateEventUserRequestDto.getStateAction() == SEND_TO_REVIEW) {
                eventEntity.setState(PENDING);
            }
        }
        this.setEventFields(eventEntity,
                updateEventUserRequestDto.getAnnotation(),
                updateEventUserRequestDto.getCategory(),
                updateEventUserRequestDto.getDescription(),
                updateEventUserRequestDto.getEventDate(),
                false,
                updateEventUserRequestDto.getLocation(),
                updateEventUserRequestDto.getPaid(),
                updateEventUserRequestDto.getParticipantLimit(),
                updateEventUserRequestDto.getRequestModeration(),
                updateEventUserRequestDto.getTitle());
        final EventFullResponseDto eventFullResponseDto = EventMapper
                .toEventFullResponseDto(
                        eventRepository.save(eventEntity));
        log.info("PRIVATE => Событие обновлено по id => {}, пользователем по id => {}", eventId, userId);
        return eventFullResponseDto;
    }

    @Override
    public List<ParticipationResponseDto> getUserEventRequests(long userId, long eventId, int from, int size) throws NotFoundException {
        userAdminService.checkUserIsExistById(userId);
        this.checkEventIsExistById(eventId);
        final Page<ParticipationResponseDto> participationRequestDtoList = requestAdminService
                .findAllByEventId(
                        eventId, getPageSortDescByProperties(from, size, "createdOn"))
                .map(RequestMapper::toParticipationResponseDto);
        log.info("PRIVATE => Список заявок size => {}, на участие в событии по id => {}, пользователем по id => {} получен",
                participationRequestDtoList.getTotalElements(), eventId, userId);
        return participationRequestDtoList.getContent();
    }

    @Transactional
    @Modifying
    @Override
    public EventResponseStatusUpdateDto changeRequestsStatus(long userId, long eventId,
                                                             EventRequestStatusUpdateDto eventRequestStatusUpdateDto)
            throws NotFoundException, ConflictException {
        userAdminService.checkUserIsExistById(userId);
        final EventEntity eventEntity = this.findEventEntityById(eventId);
        if (eventEntity.getConfirmedRequests() >= eventEntity.getParticipantLimit()) {
            throw new ConflictException("PRIVATE => Достигнут лимит заявок на участие в событии");
        }
        final Map<RequestStatus, List<ParticipationEntity>> requestEntityMap = new HashMap<>();
        final List<ParticipationEntity> participationEntityList = requestAdminService
                .findAllById(
                        eventRequestStatusUpdateDto.getRequestIds());
        for (ParticipationEntity participationEntity : participationEntityList) {
            if (participationEntity.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("PRIVATE => Статус != PENDING");
            }
            if (eventEntity.getParticipantLimit() == 0
                    || (eventEntity.getConfirmedRequests() < eventEntity.getParticipantLimit()
                    && !eventEntity.getRequestModeration())
                    || (eventEntity.getConfirmedRequests() < eventEntity.getParticipantLimit()
                    && eventRequestStatusUpdateDto.getStatus() == CONFIRMED)
            ) {
                participationEntity.setStatus(CONFIRMED);
                eventEntity.setConfirmedRequests(eventEntity.getConfirmedRequests() + 1);
            } else {
                participationEntity.setStatus(REJECTED);
            }
            requestEntityMap.computeIfAbsent(participationEntity.getStatus(),
                    v -> new ArrayList<>()).add(participationEntity);
        }
        requestAdminService.saveAllRequestEntity(participationEntityList);
        eventRepository.save(eventEntity);
        final EventResponseStatusUpdateDto eventResponseStatusUpdateDto = EventResponseStatusUpdateDto
                .builder()
                .confirmedRequests(requestEntityMap.getOrDefault(CONFIRMED, emptyList())
                        .stream()
                        .map(RequestMapper::toParticipationResponseDto)
                        .collect(toList()))
                .rejectedRequests(requestEntityMap.getOrDefault(REJECTED, emptyList())
                        .stream()
                        .map(RequestMapper::toParticipationResponseDto)
                        .collect(toList()))
                .build();
        log.info("PRIVATE => Изменение статуса события по id => {} пользователем по id => {}", eventId, userId);
        return eventResponseStatusUpdateDto;
    }

    @Override
    public List<EventShortResponseDto> getAllEvents(String text, List<Long> categories, Boolean paid,
                                                    String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                    String sort, int from, int size, HttpServletRequest httpServletRequest) throws BadRequestException {
        AtomicReference<LocalDateTime> start = new AtomicReference<>(getLocalDateTimeFormatting(rangeStart));
        LocalDateTime end = getLocalDateTimeFormatting(rangeEnd);
        checkStartIsAfterEndEvent(start.get(), end);
        PageRequest pageRequest;
        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                pageRequest = getPageSortDescByProperties(from, size, "eventDate");
            } else if (sort.equals("VIEWS")) {
                pageRequest = getPageSortDescByProperties(from, size, "views");
            } else {
                pageRequest = getPageSortDescByProperties(from, size, "id");
            }
        } else {
            pageRequest = getPageSortDescByProperties(from, size, "id");
        }
        final Page<EventEntity> eventEntityPage = eventRepository
                .findByPublic(categories, paid, start.get(), end, onlyAvailable, text, pageRequest);
        final List<Long> eventIds = new ArrayList<>();
        statisticClient.save(EventMapper.toStatisticRequest(httpServletRequest, eventIds));
        if (start.get() == null && end == null) {
            eventEntityPage.forEach(eventEntity -> {
                eventIds.add(eventEntity.getId());
                if (start.get() == null || !start.get().isAfter(eventEntity.getPublishedOn())) {
                    start.set(eventEntity.getPublishedOn());
                }
            });
            if (start.get() == null) {
                start.set(LocalDateTime.now());
            }
            end = LocalDateTime.now().plusSeconds(1L);
        } else {
            eventEntityPage.forEach(eventEntity -> eventIds.add(eventEntity.getId()));
        }
        final Map<Long, Long> eventIdViewHitMap = statisticClient.getStatistics(start.get(), end,
                        eventIds.stream()
                                .map(eventId -> httpServletRequest.getRequestURI() + "/" + eventId)
                                .collect(toList()),
                        true)
                .stream()
                .collect(toMap(v -> Long.parseLong(v.getUri().substring(
                                v.getUri().lastIndexOf("/") + 1))
                        /*ViewStatistic::getEventId => For unique views when getAllEvents*/, ViewStatistic::getHits));
        final Page<EventShortResponseDto> eventShortResponseDtoPage = eventEntityPage
                .map(eventEntity -> {
                    eventEntity.setViews(eventIdViewHitMap.getOrDefault(eventEntity.getId(), 0L));
                    return EventMapper.toEventShortResponseDto(eventEntity);
                });
        log.info("PUBLIC => Поиск событий => totalElements => {} text => {}, categories => {}, paid => {}, " +
                        "rangeStart => {}, rangeEnd => {}, onlyAvailable => {}, sort => {}, from => {}, size => {}",
                eventShortResponseDtoPage.getTotalElements(), text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventShortResponseDtoPage.getContent();
    }

    //TODO !QUERYDSL

    @Override
    public EventFullResponseDto getEventById(long eventId, HttpServletRequest httpServletRequest) throws NotFoundException {
        final EventEntity eventEntity = this.findEventEntityByIdAndStatusPublished(eventId);
        statisticClient.save(EventMapper.toStatisticRequest(httpServletRequest, emptyList()));
        final List<ViewStatistic> viewStatisticList = statisticClient.getStatistics(eventEntity.getPublishedOn(),
                LocalDateTime.now().plusSeconds(1L), List.of(httpServletRequest.getRequestURI()),
                true);
        final EventFullResponseDto eventFullResponseDto = EventMapper
                .toEventFullResponseDto(eventEntity);
        if (!viewStatisticList.isEmpty() && Objects.equals(Long.parseLong(viewStatisticList.get(0).getUri().substring(
                        viewStatisticList.get(0).getUri().lastIndexOf("/") + 1))
                /*viewStatisticList.get(0).getEventId().equals( => For unique views when getAllEvents*/, eventFullResponseDto.getId())) {
            eventFullResponseDto.setViews(viewStatisticList.get(0).getHits());
        }
        log.info("PUBLIC => Событие по id => {} получено", eventId);
        return eventFullResponseDto;
    }

    private void setEventFields(EventEntity eventEntity,
                                String annotation,
                                Long category,
                                String description,
                                LocalDateTime eventDate,
                                boolean adminOrUser,
                                LocationRequestDto location,
                                Boolean paid,
                                Long participantLimit,
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
