package ru.practicum.comment.service;

import ru.defaultComponent.ewmService.dto.comment.CommentResponseDto;
import java.util.List;

public interface CommentPublicService {

    List<CommentResponseDto> getAllComments(int from, int size);

    List<CommentResponseDto> getCommentsByEventId(long eventId, int from, int size);

}