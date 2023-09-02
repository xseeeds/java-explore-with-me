package ru.practicum.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.ewmService.dto.comment.CreateCommentRequestDto;
import ru.defaultComponent.ewmService.dto.comment.CommentResponseDto;
import ru.practicum.comment.model.CommentEntity;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.EventEntity;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.UserEntity;

import java.time.LocalDateTime;

import static ru.defaultComponent.ewmService.enums.CommentState.PENDING;

@UtilityClass
public class CommentMapper {

    public CommentEntity toNewCommentEntity(CreateCommentRequestDto createCommentRequestDto,
                                            UserEntity userEntity,
                                            EventEntity eventEntity) {
        return CommentEntity
                .builder()
                .authorEntity(userEntity)
                .author(userEntity.getId())
                .eventEntity(eventEntity)
                .event(eventEntity.getId())
                .createdOn(LocalDateTime.now())
                .state(PENDING)
                .text(createCommentRequestDto.getText())
                .build();
    }

    public CommentResponseDto toCommentResponseDto(CommentEntity commentEntity) {
        return CommentResponseDto.builder()
                .id(commentEntity.getId())
                .author(UserMapper.toUserShortResponseDto(commentEntity.getAuthorEntity()))
                .event(EventMapper.toEventShortResponseDto(commentEntity.getEventEntity()))
                .createdOn(commentEntity.getCreatedOn())
                .publishedOn(commentEntity.getPublishedOn())
                .state(commentEntity.getState())
                .text(commentEntity.getText())
                .build();
    }

}
