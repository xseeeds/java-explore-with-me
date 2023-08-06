package ru.practicum.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.defaultComponent.ewmService.dto.request.ParticipationResponseDto;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.user.service.UserAdminService;
import ru.practicum.event.model.EventEntity;
import ru.practicum.request.model.ParticipationEntity;
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
    public Page<ParticipationEntity> findAllByEventId(long eventId, Pageable page) {
        final Page<ParticipationEntity> requestEntityPage = requestRepository
                .findAllByEvent(eventId, page);
        log.info("ADMIN => Запрошен список запросов на участие size => {}, по событию id => {} SERVICE",
                requestEntityPage.getTotalElements(), eventId);
        return requestEntityPage;
    }

    @Override
    public List<ParticipationEntity> findAllById(List<Long> requestIds) {
        final List<ParticipationEntity> participationEntityList = requestRepository
                .findAllById(requestIds);
        log.info("ADMIN => Запрошен список запросов на участие size => {} SERVICE", participationEntityList.size());
        return participationEntityList;
    }

    @Override
    public ParticipationEntity findRequestEntityById(long requestId) throws NotFoundException {
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
    public void saveAllRequestEntity(List<ParticipationEntity> participationEntityList) {
        requestRepository.saveAll(participationEntityList);
        log.info("ADMIN => Запрос сохранения событий size => {} для СЕРВИСОВ", participationEntityList.size());
    }

    @Override
    public List<ParticipationResponseDto> getUserRequests(long userId, int from, int size) throws NotFoundException {
        userAdminService.checkUserIsExistById(userId);
        final Page<ParticipationResponseDto> participationRequestDtoPage = requestRepository
                .findAllByRequester(
                        userId, getPageSortAscByProperties(from, size, "id"))
                .map(RequestMapper::toParticipationResponseDto);
        log.info("PRIVATE => Запрошен список запросов на участие size => {}, для пользователя с id => {}",
                participationRequestDtoPage.getTotalElements(), userId);
        return participationRequestDtoPage.getContent();
    }

    @Transactional
    @Modifying
    @Override
    public ParticipationResponseDto createRequest(long userId, long eventId) throws NotFoundException, ConflictException {
        userAdminService.checkUserIsExistById(userId);
        final EventEntity eventEntity = eventAdminService.findEventEntityById(eventId);
        final ParticipationEntity participationEntity = ParticipationEntity
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
        if (eventEntity.getConfirmedRequests() != 0 && eventEntity.getConfirmedRequests() >= eventEntity.getParticipantLimit()) {
            throw new ConflictException("PRIVATE => Достигнут лимит заявок на участие в событии");
        }
        if (eventEntity.getParticipantLimit() == 0 || !eventEntity.getRequestModeration()) {
            eventEntity.setConfirmedRequests(eventEntity.getConfirmedRequests() + 1);
            eventAdminService.saveEventEntity(eventEntity);
            participationEntity.setStatus(CONFIRMED);
        }
        final ParticipationResponseDto participationResponseDto = RequestMapper
                .toParticipationResponseDto(
                        requestRepository.save(participationEntity));
        log.info("PRIVATE => Сохранен запрос на участие пользователем по id => {} в событии с id => {}", userId, eventId);
        return participationResponseDto;
    }

    @Transactional
    @Modifying
    @Override
    public ParticipationResponseDto cancelRequest(long userId, long requestId) throws NotFoundException {
        userAdminService.checkUserIsExistById(userId);
        final ParticipationEntity participationEntity = this.findRequestEntityById(requestId);
        participationEntity.setStatus(CANCELED);
        final ParticipationResponseDto participationResponseDto = RequestMapper
                .toParticipationResponseDto(participationEntity);
        log.info("PRIVATE => Отмена запроса на участие в событии по id => {} пользователем по id => {}", requestId, userId);
        return participationResponseDto;
    }

}
