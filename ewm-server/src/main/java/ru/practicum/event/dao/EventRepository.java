package ru.practicum.event.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.defaultComponent.ewmServer.enums.EventState;
import ru.practicum.event.model.EventEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("SELECT ee FROM EventEntity AS ee " +
            "WHERE (:users IS NULL OR ee.initiator IN :users OR ee.initiator IS NULL) " +
            "AND (:categories IS NULL OR ee.category IN :categories OR ee.category IS NULL) " +
            "AND (:states IS NULL OR ee.state IN :states OR ee.state IS NULL) " +
            "AND (coalesce(:start, ee.eventDate) <= ee.eventDate AND coalesce(:end, ee.eventDate) >= ee.eventDate)")
    Page<EventEntity> findByAdmin(@Param("users") List<Long> users, @Param("categories") List<Long> categories,
                                  @Param("states") List<EventState> states, @Param("start") LocalDateTime rangeStart,
                                  @Param("end") LocalDateTime rangeEnd, Pageable page);

    boolean existsByCategory(Long categoryId);

    Page<EventEntity> findAllByInitiator(long eventId, Pageable page);

    @Query("SELECT ee FROM EventEntity AS ee " +
            "WHERE ee.state = ru.defaultComponent.ewmServer.enums.EventState.PUBLISHED " +
            "AND (:categories IS NULL OR ee.category IN :categories OR ee.category IS NULL)" +
            "AND (:paid IS NULL OR ee.paid = :paid OR ee.paid IS NULL) " +
            "AND (coalesce(:start, ee.eventDate) <= ee.eventDate AND coalesce(:end, ee.eventDate) >= ee.eventDate)" +
            "AND (:onlyAvailable IS NULL OR ((:onlyAvailable = TRUE AND ee.confirmedRequests < ee.participantLimit) " +
            "OR (:onlyAvailable = FALSE AND 1 = 1))) " +
            "AND ((:text IS NULL) " +
            "OR upper(ee.description) LIKE upper(concat('%',:text,'%')) " +
            "OR upper(ee.annotation) LIKE upper(concat('%',:text,'%')) )")
    Page<EventEntity> findByPublic(@Param("categories") List<Long> categories, @Param("paid") Boolean paid,
                                   @Param("start") LocalDateTime rangeStart, @Param("end") LocalDateTime rangeEnd,
                                   @Param("onlyAvailable") Boolean onlyAvailable, @Param("text") String text, Pageable page);

    Optional<EventEntity> findByIdAndState(long eventId, EventState state);

}
