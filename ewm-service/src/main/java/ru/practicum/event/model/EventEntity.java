package ru.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.defaultComponent.ewmService.enums.EventState;
import ru.practicum.category.model.CategoryEntity;
import ru.practicum.user.model.UserEntity;
import javax.persistence.GenerationType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.EnumType;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 2000, nullable = false)
    String annotation;

    @ManyToOne(targetEntity = CategoryEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false, unique = true, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonBackReference
    @ToString.Exclude
    CategoryEntity categoryEntity;

    @Column(name = "category_id")
    Long category;

    @Builder.Default
    @Column(name = "confirmed_requests")
    Long confirmedRequests = 0L;

    @Column(name = "created_date", nullable = false)
    LocalDateTime createdOn;

    @Column(length = 7000, nullable = false)
    String description;

    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", referencedColumnName = "id", nullable = false, unique = true, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    @ToString.Exclude
    UserEntity initiatorEntity;

    @Column(name = "initiator_id")
    Long initiator;

    @Embedded
    @Column(nullable = false)
    Location location;

    @Column(nullable = false)
    Boolean paid;

    @Column(name = "participant_limit")
    Long participantLimit;

    @Column(name = "published_date")
    LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    EventState state;

    @Column(length = 128)
    String title;

    @Builder.Default
    Long views = 0L;

}

//TODO пришел к такому выводу так думаю намного проще, что то я перемудрил) можно потренировать другой подход
/*
@ManyToMany(targetEntity = CommentEntity.class, fetch = FetchType.LAZY)
@JoinTable(name = "event_comments",
        joinColumns = @JoinColumn(name = "comment_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id"))
List<CommentEntity> comments;

commentEntity.of(id, authorShort(id, name), text)
или вообще => in userEntity

@ManyToMany(targetEntity = CommentEntity.class, fetch = FetchType.LAZY)
@JoinTable(name = "user_comments",
        joinColumns = @JoinColumn(name = "comment_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
List<CommentEntity> comments;
*/