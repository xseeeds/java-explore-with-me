package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.client.StatisticClient;
import ru.defaultComponent.ewmService.dto.event.*;
import ru.defaultComponent.ewmService.dto.participation.ParticipationResponseUpdateStateDto;
import ru.defaultComponent.ewmService.dto.participation.ParticipationRequestUpdateStateDto;
import ru.defaultComponent.ewmService.dto.participation.ParticipationResponseDto;
import ru.defaultComponent.ewmService.enums.EventState;
import ru.defaultComponent.ewmService.enums.RequestState;
import ru.defaultComponent.exception.exp.BadRequestException;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.defaultComponent.statisticServer.dto.ViewStatistic;
import ru.practicum.category.model.CategoryEntity;
import ru.practicum.category.service.CategoryAdminService;
import ru.practicum.participation.model.ParticipationEntity;
import ru.practicum.participation.service.ParticipationAdminService;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.service.UserAdminService;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.participation.mapper.ParticipationMapper;
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
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static ru.defaultComponent.dateTime.DefaultDateTimeFormatter.getLocalDateTimeFormatting;
import static ru.defaultComponent.ewmService.enums.EventState.*;
import static ru.defaultComponent.ewmService.enums.RequestAdminState.*;
import static ru.defaultComponent.ewmService.enums.RequestUserState.*;
import static ru.defaultComponent.ewmService.enums.RequestState.CONFIRMED;
import static ru.defaultComponent.ewmService.enums.RequestState.REJECTED;
import static ru.defaultComponent.pageRequest.UtilPage.getPageSortDescByProperties;
import static ru.defaultComponent.dateTime.CheckLocalDateTime.checkEventDateToUpdateEventAdmin;
import static ru.defaultComponent.dateTime.CheckLocalDateTime.checkEventDateToAddEventPrivate;
import static ru.defaultComponent.dateTime.CheckLocalDateTime.checkEventDateToUpdateEventPrivate;
import static ru.defaultComponent.dateTime.CheckLocalDateTime.checkStartIsAfterEndMayBeNull;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventAdminService, EventPrivateService, EventPublicService {

    private final EventRepository eventRepository;
    private final UserAdminService userAdminService;
    private final CategoryAdminService categoryAdminService;
    private final ParticipationAdminService participationAdminService;
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
        checkEventDateToUpdateEventAdmin(updateEventAdminRequestDto.getEventDate());
        final EventEntity eventEntity = this.findEventEntityById(eventId);
        checkEventDateToUpdateEventAdmin(eventEntity.getEventDate());
        if (updateEventAdminRequestDto.getStateAction() != null) {
            if (eventEntity.getState() != PENDING && updateEventAdminRequestDto.getStateAction() == PUBLISH_EVENT) {
                throw new ConflictException("ADMIN => Событие уже рассмотрено");
            }
            if (eventEntity.getState() == PUBLISHED) {
                throw new ConflictException("ADMIN => Событие уже опубликовано");
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
        log.info("ADMIN => запрос события по id => {}", eventId);
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        "ADMIN => Событие по id => " + eventId + " не существует"));
    }

    @Override
    public EventEntity findEventEntityByIdAndStatusPublished(long eventId) throws NotFoundException {
        log.info("ADMIN => запрос события со статусом PUBLISHED по id => {}", eventId);
        return eventRepository.findByIdAndState(eventId, PUBLISHED)
                .orElseThrow(() -> new NotFoundException(
                        "PUBLIC => Событие по id => " + eventId + " ещё не опубликовано"));
    }

    @Override
    public void checkEventEntityIsExistById(long eventId) throws NotFoundException {
        log.info("ADMIN => Запрос существует событие по id => {}", eventId);
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("ADMIN => Событие по id => " + eventId + " не существует");
        }
    }

    @Transactional
    @Modifying
    @Override
    public void saveEventEntity(EventEntity eventEntity) {
        eventRepository.save(eventEntity);
        log.info("ADMIN => Запрос сохранения события по id => {}", eventEntity.getId());
    }

    @Override
    public void checkEventsByCategoryId(long categoryId) throws ConflictException {
        log.info("ADMIN => Категория по id => {} связана с событием", categoryId);
        if (eventRepository.existsByCategory(categoryId)) {
            throw new ConflictException("ADMIN => Категория по id => " + categoryId + " связана с событием");
        }
    }

    @Transactional
    @Modifying
    @Override
    public EventFullResponseDto addNewEvent(long userId, CreateEventRequestDto createEventRequestDto)
            throws BadRequestException, NotFoundException {
        checkEventDateToAddEventPrivate(createEventRequestDto.getEventDate());
        final UserEntity userEntity = userAdminService.findUserEntityById(userId);
        final CategoryEntity categoryEntity = categoryAdminService.findCategoryEntityById(
                createEventRequestDto.getCategory());
        final EventEntity eventEntity = EventMapper
                .toNewEventEntity(
                        createEventRequestDto, userEntity, categoryEntity);
        final EventFullResponseDto eventFullResponseDto = EventMapper
                .toEventFullResponseDto(
                        eventRepository.save(eventEntity));
        log.info("PRIVATE => Создание нового события => {}, пользователем по id => {}", eventFullResponseDto, userId);
        return eventFullResponseDto;
    }

    @Override
    public List<EventShortResponseDto> getAllUserEvents(long userId, int from, int size) throws NotFoundException {
        userAdminService.checkUserEntityIsExistById(userId);
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
        userAdminService.checkUserEntityIsExistById(userId);
        final EventFullResponseDto eventFullResponseDto = EventMapper
                .toEventFullResponseDto(
                        this.findEventEntityById(eventId));
        log.info("PRIVATE => Информация о событии по id => {}, получена пользователем по id => {}", eventId, userId);
        return eventFullResponseDto;
    }

    @Transactional
    @Modifying
    @Override
    public EventFullResponseDto updateEventByUser(long userId, long eventId,
                                                  UpdateEventUserRequestDto updateEventUserRequestDto)
            throws BadRequestException, NotFoundException, ConflictException {
        userAdminService.checkUserEntityIsExistById(userId);
        checkEventDateToUpdateEventPrivate(updateEventUserRequestDto.getEventDate());
        final EventEntity eventEntity = this.findEventEntityById(eventId);
        if (eventEntity.getState() == PUBLISHED) {
            throw new ConflictException("PRIVATE => Событие уже опубликовано");
        }
        checkEventDateToUpdateEventPrivate(eventEntity.getEventDate());
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
    public List<ParticipationResponseDto> findAllParticipationByEventId(long userId, long eventId, int from, int size)
            throws NotFoundException {
        userAdminService.checkUserEntityIsExistById(userId);
        this.checkEventEntityIsExistById(eventId);
        final Page<ParticipationResponseDto> participationRequestDtoList = participationAdminService
                .findAllParticipationByEventId(
                        eventId, getPageSortDescByProperties(from, size, "createdOn"))
                .map(ParticipationMapper::toParticipationResponseDto);
        log.info("PRIVATE => Список заявок size => {}, на участие в событии по id => {}, пользователем по id => {} получен",
                participationRequestDtoList.getTotalElements(), eventId, userId);
        return participationRequestDtoList.getContent();
    }

    @Transactional
    @Modifying
    @Override
    public ParticipationResponseUpdateStateDto changeParticipationsState(long userId, long eventId,
                                                                         ParticipationRequestUpdateStateDto participationRequestUpdateStateDto)
            throws NotFoundException, ConflictException {
        userAdminService.checkUserEntityIsExistById(userId);
        final EventEntity eventEntity = this.findEventEntityById(eventId);
        if (eventEntity.getConfirmedRequests() >= eventEntity.getParticipantLimit()) {
            throw new ConflictException("PRIVATE => Достигнут лимит заявок на участие в событии");
        }
        final Map<RequestState, List<ParticipationEntity>> requestEntityMap = new HashMap<>();
        final List<ParticipationEntity> participationEntityList = participationAdminService
                .findAllParticipationById(
                        participationRequestUpdateStateDto.getRequestIds());
        for (ParticipationEntity participationEntity : participationEntityList) {
            if (participationEntity.getState() != RequestState.PENDING) {
                throw new ConflictException("PRIVATE => Заявка на участие уже рассмотрена");
            }
            if (eventEntity.getParticipantLimit() == 0
                    || (eventEntity.getConfirmedRequests() < eventEntity.getParticipantLimit()
                    && !eventEntity.getRequestModeration())
                    || (eventEntity.getConfirmedRequests() < eventEntity.getParticipantLimit()
                    && participationRequestUpdateStateDto.getStatus() == CONFIRMED)
            ) {
                participationEntity.setState(CONFIRMED);
                eventEntity.setConfirmedRequests(eventEntity.getConfirmedRequests() + 1);
            } else {
                participationEntity.setState(REJECTED);
            }
            requestEntityMap.computeIfAbsent(participationEntity.getState(),
                    v -> new ArrayList<>()).add(participationEntity);
        }
        participationAdminService.saveAllParticipationEntity(participationEntityList);
        eventRepository.save(eventEntity);
        final ParticipationResponseUpdateStateDto participationResponseUpdateStateDto = ParticipationResponseUpdateStateDto
                .builder()
                .confirmedRequests(requestEntityMap.getOrDefault(CONFIRMED, emptyList())
                        .stream()
                        .map(ParticipationMapper::toParticipationResponseDto)
                        .collect(toList()))
                .rejectedRequests(requestEntityMap.getOrDefault(REJECTED, emptyList())
                        .stream()
                        .map(ParticipationMapper::toParticipationResponseDto)
                        .collect(toList()))
                .build();
        log.info("PRIVATE => Изменение статуса события по id => {} пользователем по id => {}", eventId, userId);
        return participationResponseUpdateStateDto;
    }

    @Override
    public List<EventShortResponseDto> getAllEvents(String text, List<Long> categories, Boolean paid,
                                                    String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                    String sort, int from, int size, HttpServletRequest httpServletRequest)
            throws BadRequestException {
        AtomicReference<LocalDateTime> start = new AtomicReference<>(getLocalDateTimeFormatting(rangeStart));
        LocalDateTime end = getLocalDateTimeFormatting(rangeEnd);
        checkStartIsAfterEndMayBeNull(start.get(), end);
        final PageRequest pageRequest;
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
        statisticClient.saveStatistic(EventMapper.toStatisticRequest(httpServletRequest, eventIds));
        if (start.get() == null && end == null) {
            eventEntityPage.forEach(eventEntity -> {
                eventIds.add(eventEntity.getId());
                if (start.get() == null ||
                        (eventEntity.getPublishedOn() != null && !start.get().isAfter(eventEntity.getPublishedOn()))
                ) {
                    start.set(eventEntity.getPublishedOn());
                }
            });
            end = LocalDateTime.now().plusSeconds(1L);
        } else {
            eventEntityPage.forEach(eventEntity -> eventIds.add(eventEntity.getId()));
        }
        final Map<Long, Long> eventIdViewHitMap;
        if (start.get() != null) {
            eventIdViewHitMap = statisticClient.getStatistics(start.get(), end,
                            eventIds.stream()
                                    .map(eventId -> httpServletRequest.getRequestURI() + "/" + eventId)
                                    .collect(toList()),
                            true)
                    .stream()
                    .collect(toMap(v -> Long.parseLong(v.getUri().substring(
                            v.getUri().lastIndexOf("/") + 1)), ViewStatistic::getHits));
        } else {
            eventIdViewHitMap = emptyMap();
        }
        final Page<EventShortResponseDto> eventShortResponseDtoPage = eventEntityPage
                .map(eventEntity -> {
                    eventEntity.setViews(eventIdViewHitMap.getOrDefault(eventEntity.getId(), 0L));
                    return EventMapper.toEventShortResponseDto(eventEntity);
                });
        log.info("PUBLIC => Поиск событий => totalElements => {} text => {}, categories => {}, paid => {}, " +
                        "rangeStart => {}, rangeEnd => {}, onlyAvailable => {}, sort => {}, from => {}, size => {}",
                eventShortResponseDtoPage.getTotalElements(), text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size);
        return eventShortResponseDtoPage.getContent();
    }

    @Override
    public EventFullResponseDto getEventById(long eventId, HttpServletRequest httpServletRequest) throws NotFoundException {
        final EventEntity eventEntity = this.findEventEntityByIdAndStatusPublished(eventId);
        statisticClient.saveStatistic(EventMapper.toStatisticRequest(httpServletRequest, emptyList()));
        final List<ViewStatistic> viewStatisticList = statisticClient.getStatistics(eventEntity.getPublishedOn(),
                LocalDateTime.now().plusSeconds(1L), List.of(httpServletRequest.getRequestURI()),
                true);
        final EventFullResponseDto eventFullResponseDto = EventMapper
                .toEventFullResponseDto(eventEntity);
        if (!viewStatisticList.isEmpty() && Objects.equals(Long.parseLong(viewStatisticList.get(0).getUri().substring(
                viewStatisticList.get(0).getUri().lastIndexOf("/") + 1)), eventFullResponseDto.getId())) {
            eventFullResponseDto.setViews(viewStatisticList.get(0).getHits());
        }
        log.info("PUBLIC => Событие по id => {} получено", eventId);
        return eventFullResponseDto;
    }

    private void setEventFields(final EventEntity eventEntity,
                                final String annotation,
                                final Long category,
                                final String description,
                                final LocalDateTime eventDate,
                                final LocationRequestDto location,
                                final Boolean paid,
                                final Long participantLimit,
                                final Boolean requestModeration,
                                final String title) {
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
        if (requestModeration != null) {
            eventEntity.setRequestModeration(requestModeration);
        }
        if (title != null) {
            eventEntity.setTitle(title);
        }
    }

}
