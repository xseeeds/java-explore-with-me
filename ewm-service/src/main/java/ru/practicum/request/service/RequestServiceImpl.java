package ru.practicum.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.defaultComponent.ewmService.dto.event.ParticipationRequestDto;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.user.service.UserAdminService;
import ru.practicum.event.model.EventEntity;
import ru.practicum.request.model.RequestEntity;
import ru.practicum.event.service.EventAdminService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.defaultComponent.ewmService.enums.EventState.PUBLISHED;
import static ru.defaultComponent.ewmService.enums.RequestStatus.*;
import static ru.defaultComponent.pageRequest.UtilPage.getPageSortAscByProperties;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestAdminService, RequestPrivateService {

    private final RequestRepository requestRepository;
    private final EventAdminService eventAdminService;
    private final UserAdminService userAdminService;

    public RequestServiceImpl(RequestRepository requestRepository,
                              @Lazy EventAdminService eventAdminService,
                              UserAdminService userAdminService) {
        this.requestRepository = requestRepository;
        this.eventAdminService = eventAdminService;
        this.userAdminService = userAdminService;
    }

    @Override
    public Page<RequestEntity> findAllByEventId(long eventId, Pageable page) {
        final Page<RequestEntity> requestEntityPage = requestRepository
                .findAllByEvent(eventId, page);
        log.info("ADMIN => Запрошен список запросов на участие size => {}, по событию id => {} SERVICE",
                requestEntityPage.getTotalElements(), eventId);
        return requestEntityPage;
    }

    @Override
    public List<RequestEntity> findAllById(List<Long> requestIds) {
        final List<RequestEntity> requestEntityList = requestRepository
                .findAllById(requestIds);
        log.info("ADMIN => Запрошен список запросов на участие size => {} SERVICE", requestEntityList.size());
        return requestEntityList;
    }

    @Override
    public RequestEntity findRequestEntityById(long requestId) throws NotFoundException {
        log.info("ADMIN => Запрос на участие в событии по id => {} получен для СЕРВИСОВ", requestId);
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        "ADMIN => Запрос на участие в событии по id => " + requestId + " не существует поиск СЕРВИСОВ"));
    }

    @Override
    public void checkRequestIsExistById(long requestId) throws NotFoundException {
        log.info("ADMIN => Запрос на участие в событии по id => {} для СЕРВИСОВ", requestId);
        if (!requestRepository.existsById(requestId)) {
            throw new NotFoundException("ADMIN => Запрос на участие в событии по id => " + requestId + " не существует");
        }
    }

    @Transactional
    @Modifying
    @Override
    public void saveAllRequestEntity(List<RequestEntity> requestEntityList) {
        requestRepository.saveAll(requestEntityList);
        log.info("ADMIN => Запрос сохранения событий size => {} для СЕРВИСОВ", requestEntityList.size());
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId, int from, int size) throws NotFoundException {
        userAdminService.checkUserIsExistById(userId);
        final Page<ParticipationRequestDto> participationRequestDtoPage = requestRepository
                .findAllByRequester(
                        userId, getPageSortAscByProperties(from, size, "id"))
                .map(RequestMapper::toRequestDto);
        log.info("PRIVATE => Запрошен список запросов на участие size => {}, для пользователя с id => {}",
                participationRequestDtoPage.getTotalElements(), userId);
        return participationRequestDtoPage.getContent();
    }

    @Transactional
    @Modifying
    @Override
    public ParticipationRequestDto createRequest(long userId, long eventId) throws NotFoundException, ConflictException {
        userAdminService.checkUserIsExistById(userId);
        final EventEntity eventEntity = eventAdminService.findEventEntityById(eventId);
        final RequestEntity requestEntity = RequestEntity
                .builder()
                .createdOn(LocalDateTime.now())
                .event(eventId)
                .requester(userId)
                .status(PENDING)
                .build();
        if (eventEntity.getState() != PUBLISHED) {
            throw new ConflictException("PRIVATE => Событие ещё не опубликовано");
        }
        if (eventEntity.getInitiator().equals(userId)) {
            throw new ConflictException("PRIVATE => Инициатор события не может создать запрос на участие");
        }
        if (requestRepository.existsByRequesterAndEvent(userId, eventId)) {
            throw new ConflictException("PRIVATE => Повторный запрос на участие в событии");
        }
        int confirmed = eventEntity.getConfirmedRequests();
        int limit = eventEntity.getParticipantLimit();
        if (limit == 0) {
            eventEntity
                    .setConfirmedRequests(++confirmed);
            eventAdminService
                    .saveEventEntity(eventEntity);
            requestEntity
                    .setStatus(CONFIRMED);
        } else if (confirmed < limit) {
            if (!eventEntity.getRequestModeration()) {
                eventEntity
                        .setConfirmedRequests(++confirmed);
                eventAdminService
                        .saveEventEntity(eventEntity);
                requestEntity
                        .setStatus(CONFIRMED);
            }
        } else {
            throw new ConflictException("PRIVATE => Достигнут лимит заявок на участие в событии");
        }
        final ParticipationRequestDto participationRequestDto = RequestMapper
                .toRequestDto(
                        requestRepository.save(requestEntity));
        log.info("PRIVATE => Сохранен запрос на участие пользователем по id => {} в событии с id => {}", userId, eventId);
        return participationRequestDto;
    }

    @Transactional
    @Modifying
    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) throws NotFoundException {
        userAdminService.checkUserIsExistById(userId);
        final RequestEntity requestEntity = this.findRequestEntityById(requestId);
        requestEntity.setStatus(CANCELED);
        final ParticipationRequestDto participationRequestDto = RequestMapper
                .toRequestDto(requestEntity);
        log.info("PRIVATE => Отмена запроса на участие в событии по id => {} пользователем по id => {}", requestId, userId);
        return participationRequestDto;
    }

}
