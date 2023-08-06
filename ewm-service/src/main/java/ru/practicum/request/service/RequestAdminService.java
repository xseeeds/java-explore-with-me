package ru.practicum.request.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.request.model.ParticipationEntity;

import java.util.List;

public interface RequestAdminService {

    Page<ParticipationEntity> findAllByEventId(long eventId, Pageable page);

    List<ParticipationEntity> findAllById(List<Long> requestIds);

    ParticipationEntity findRequestEntityById(long requestId) throws NotFoundException;

    void checkRequestIsExistById(long requestId) throws NotFoundException;

    void saveAllRequestEntity(List<ParticipationEntity> participationEntityList);

}
