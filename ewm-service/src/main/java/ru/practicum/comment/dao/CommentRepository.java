package ru.practicum.comment.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.defaultComponent.ewmService.enums.CommentState;
import ru.practicum.comment.model.CommentEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query("SELECT ce FROM CommentEntity AS ce " +
            "WHERE (:users IS NULL OR ce.author IN :users OR ce.author IS NULL) " +
            "AND (:states is null OR ce.state IN :states OR ce.state IS NULL) " +
            "AND (:events is null OR ce.event IN :events OR ce.event IS NULL) " +
            "AND (coalesce(:start, ce.createdOn) <= ce.createdOn AND coalesce(:end, ce.createdOn) >= ce.createdOn)")
    Page<CommentEntity> findByAdmin(@Param("users") List<Long> users, @Param("states") List<CommentState> states,
                                    @Param("events") List<Long> events, @Param("start") LocalDateTime rangeStart,
                                    @Param("end") LocalDateTime rangeEnd, Pageable pageable);

    Optional<CommentEntity> findByIdAndAuthor(long commentId, long authorId);

    Page<CommentEntity> findAllByState(CommentState status, Pageable pageable);

    Page<CommentEntity> findCommentsByEventAndState(Long eventId, CommentState commentState, Pageable pageable);

    boolean existsByIdAndAuthor(long commentId, long authorId);

}