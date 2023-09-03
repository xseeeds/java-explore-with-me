package ru.practicum.participation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.defaultComponent.ewmServer.dto.participation.ParticipationResponseDto;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.participation.mapper.ParticipationMapper;
import ru.practicum.participation.dao.ParticipationRepository;
import ru.practicum.user.service.UserAdminService;
import ru.practicum.event.model.EventEntity;
import ru.practicum.participation.model.ParticipationEntity;
import ru.practicum.event.service.EventAdminService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.defaultComponent.ewmServer.enums.EventState.PUBLISHED;
import static ru.defaultComponent.ewmServer.enums.RequestState.*;
import static ru.defaultComponent.pageRequest.UtilPage.getPageSortAscByProperties;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ParticipationServiceImpl implements ParticipationAdminService, ParticipationPrivateService {

    private final ParticipationRepository participationRepository;
    private final EventAdminService eventAdminService;
    private final UserAdminService userAdminService;

    public ParticipationServiceImpl(ParticipationRepository participationRepository,
                                    @Lazy EventAdminService eventAdminService,
                                    UserAdminService userAdminService) {
        this.participationRepository = participationRepository;
        this.eventAdminService = eventAdminService;
        this.userAdminService = userAdminService;
    }

    @Override
    public Page<ParticipationEntity> findAllParticipationByEventId(long eventId, Pageable page) {
        final Page<ParticipationEntity> requestEntityPage = participationRepository
                .findAllByEvent(eventId, page);
        log.info("ADMIN => Запрошен список запросов на участие size => {} в событии по id => {} SERVICE",
                requestEntityPage.getTotalElements(), eventId);
        return requestEntityPage;
    }

    @Override
    public List<ParticipationEntity> findAllParticipationById(List<Long> participationIds) {
        final List<ParticipationEntity> participationEntityList = participationRepository
                .findAllById(participationIds);
        log.info("ADMIN => Запрошен список запросов на участие size => {} SERVICE", participationEntityList.size());
        return participationEntityList;
    }

    @Override
    public ParticipationEntity findParticipationEntityById(long participationId) throws NotFoundException {
        log.info("ADMIN => Запрос на участие в событии по id => {} получен", participationId);
        return participationRepository.findById(participationId)
                .orElseThrow(() -> new NotFoundException(
                        "ADMIN => Запрос на участие в событии по id => " + participationId + " не существует"));
    }

    @Override
    public void checkParticipationEntityIsExistById(long participationId) throws NotFoundException {
        log.info("ADMIN => Запрос на участие в событии по id => {}", participationId);
        if (!participationRepository.existsById(participationId)) {
            throw new NotFoundException("ADMIN => Запрос на участие в событии по id => " + participationId + " не существует");
        }
    }

    @Override
    public void checkReParticipationInEvent(long userId, long eventId) {
        log.info("ADMIN => Зарос проверка на повторное участие пользователя по id => {} в событии по id => {}",
                userId, eventId);
        if (participationRepository.existsByRequesterAndEvent(userId, eventId)) {
            throw new ConflictException("ADMIN => Повторное участие пользователя по id => " + userId
                    + " в событии по id => " + eventId);
        }
    }

    @Transactional
    @Modifying
    @Override
    public void saveAllParticipationEntity(List<ParticipationEntity> participationEntityList) {
        participationRepository.saveAll(participationEntityList);
        log.info("ADMIN => Запрос сохранения заявок нва участие событий size => {}", participationEntityList.size());
    }

    @Override
    public List<ParticipationResponseDto> getUserParticipation(long userId, int from, int size) throws NotFoundException {
        userAdminService.checkUserEntityIsExistById(userId);
        final Page<ParticipationResponseDto> participationRequestDtoPage = participationRepository
                .findAllByRequester(
                        userId, getPageSortAscByProperties(from, size, "id"))
                .map(ParticipationMapper::toParticipationResponseDto);
        log.info("PRIVATE => Запрошен список запросов на участие size => {}, для пользователя по id => {}",
                participationRequestDtoPage.getTotalElements(), userId);
        return participationRequestDtoPage.getContent();
    }

    @Transactional
    @Modifying
    @Override
    public ParticipationResponseDto createParticipation(long userId, long eventId) throws NotFoundException, ConflictException {
        userAdminService.checkUserEntityIsExistById(userId);
        checkReParticipationInEvent(userId, eventId);
        final EventEntity eventEntity = eventAdminService.findEventEntityById(eventId);
        if (eventEntity.getState() != PUBLISHED) {
            throw new ConflictException("PRIVATE => Событие ещё не опубликовано");
        }
        if (eventEntity.getInitiator() == userId) {
            throw new ConflictException("PRIVATE => Инициатор события не может создать запрос на участие");
        }
        if (eventEntity.getConfirmedRequests() != 0 && eventEntity.getConfirmedRequests() >= eventEntity.getParticipantLimit()) {
            throw new ConflictException("PRIVATE => Достигнут лимит заявок на участие в событии");
        }
        final ParticipationEntity participationEntity = ParticipationEntity
                .builder()
                .createdOn(LocalDateTime.now())
                .event(eventId)
                .requester(userId)
                .state(PENDING)
                .build();
        if (eventEntity.getParticipantLimit() == 0 || !eventEntity.getRequestModeration()) {
            eventEntity.setConfirmedRequests(eventEntity.getConfirmedRequests() + 1);
            eventAdminService.saveEventEntity(eventEntity);
            participationEntity.setState(CONFIRMED);
        }
        final ParticipationResponseDto participationResponseDto = ParticipationMapper
                .toParticipationResponseDto(
                        participationRepository.save(participationEntity));
        log.info("PRIVATE => Сохранен запрос на участие пользователем по id => {} в событии по id => {}", userId, eventId);
        return participationResponseDto;
    }

    @Transactional
    @Modifying
    @Override
    public ParticipationResponseDto cancelParticipation(long userId, long participationId) throws NotFoundException {
        userAdminService.checkUserEntityIsExistById(userId);
        final ParticipationEntity participationEntity = this.findParticipationEntityById(participationId);
        participationEntity.setState(CANCELED);
        final ParticipationResponseDto participationResponseDto = ParticipationMapper
                .toParticipationResponseDto(participationEntity);
        log.info("PRIVATE => Отмена заявки на участие в событии по id => {} пользователем по id => {}", participationId, userId);
        return participationResponseDto;
    }

}
