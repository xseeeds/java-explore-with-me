package ru.practicum.participation.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.participation.model.ParticipationEntity;

@Repository
public interface ParticipationRepository extends JpaRepository<ParticipationEntity, Long> {

    Page<ParticipationEntity> findAllByEvent(long eventId, Pageable page);

    Page<ParticipationEntity> findAllByRequester(long userId, Pageable page);

    boolean existsByRequesterAndEvent(long userId, long eventId);

}
