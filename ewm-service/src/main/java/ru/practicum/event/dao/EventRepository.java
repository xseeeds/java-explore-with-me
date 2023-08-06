package ru.practicum.event.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.defaultComponent.ewmService.enums.EventState;
import ru.practicum.event.model.EventEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("select ee from EventEntity as ee " +
            "where (:users is null or ee.initiator in :users or ee.initiator is null) " +
            "and (:categories is null or ee.category in :categories or ee.category is null) " +
            "and (:states is null or ee.state in :states or ee.state is null) " +
            "and (coalesce(:start, ee.eventDate) <= ee.eventDate and coalesce(:end, ee.eventDate) >= ee.eventDate)")
    Page<EventEntity> findByAdmin(@Param("users") List<Long> users, @Param("categories") List<Long> categories,
                                  @Param("states") List<EventState> states, @Param("start") LocalDateTime rangeStart,
                                  @Param("end") LocalDateTime rangeEnd, Pageable page);

    Page<EventEntity> findAllByInitiator(long eventId, Pageable page);

    @Query("select ee from EventEntity as ee " +
            "where ee.state = ru.defaultComponent.ewmService.enums.EventState.PUBLISHED " +
            "and (:categories is null or ee.category in :categories or ee.category is null)" +
            "and (:paid is null or ee.paid = :paid or ee.paid is null) " +
            "and (coalesce(:start, ee.eventDate) <= ee.eventDate and coalesce(:end, ee.eventDate) >= ee.eventDate)" +
            "and (:onlyAvailable is null or ((:onlyAvailable = true and ee.confirmedRequests < ee.participantLimit) " +
            "or (:onlyAvailable = false and 1 = 1))) " +
            "and ((:text is null) " +
            "or upper(ee.description) like upper(concat('%',:text,'%')) " +
            "or upper(ee.annotation) like upper(concat('%',:text,'%')) )")
    Page<EventEntity> findByPublic(@Param("categories") List<Long> categories, @Param("paid") Boolean paid,
                                   @Param("start") LocalDateTime rangeStart, @Param("end") LocalDateTime rangeEnd,
                                   @Param("onlyAvailable") Boolean onlyAvailable, @Param("text") String text, Pageable page);

    Optional<EventEntity> findByIdAndState(long eventId, EventState state);

}
