package ru.practicum.request.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.request.model.RequestEntity;

import java.util.List;

public interface RequestAdminService {

    Page<RequestEntity> findAllByEventId(long eventId, Pageable page);

    List<RequestEntity> findAllById(List<Long> requestIds);

    RequestEntity findRequestEntityById(long requestId) throws NotFoundException;

    void checkRequestIsExistById(long requestId) throws NotFoundException;

    void saveAllRequestEntity(List<RequestEntity> requestEntityList);

}
