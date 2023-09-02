package ru.practicum.participation.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.participation.model.ParticipationEntity;
import java.util.List;

public interface ParticipationAdminService {

    Page<ParticipationEntity> findAllParticipationByEventId(long eventId, Pageable page);

    List<ParticipationEntity> findAllParticipationById(List<Long> participationIds);

    ParticipationEntity findParticipationEntityById(long participationId) throws NotFoundException;

    void checkParticipationEntityIsExistById(long participationId) throws NotFoundException;

    void checkReParticipationInEvent(long userId, long eventId);

    void saveAllParticipationEntity(List<ParticipationEntity> participationEntityList);

}
