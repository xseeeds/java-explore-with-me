package ru.practicum.request.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.RequestEntity;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {

    Page<RequestEntity> findAllByEvent(long eventId, Pageable page);

    Page<RequestEntity> findAllByRequester(long userId, Pageable page);

    boolean existsByRequesterAndEvent(long userId, long eventId);

}
