package ru.practicum.comment.service;

import ru.defaultComponent.ewmServer.dto.comment.CommentResponseDto;
import ru.defaultComponent.ewmServer.dto.comment.CreateCommentRequestDto;
import ru.defaultComponent.ewmServer.dto.comment.UpdateCommentUserRequestDto;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;

public interface CommentPrivateService {

    CommentResponseDto createComment(long eventId, long userId, CreateCommentRequestDto createCommentRequestDto)
            throws NotFoundException, ConflictException;

    CommentResponseDto getCommentByIdAndUserId(long commentId, long userId) throws ConflictException;

    CommentResponseDto updateComment(long commentId, long userId, UpdateCommentUserRequestDto updateCommentUserRequestDto)
            throws ConflictException;

    void deleteComment(long commentId, long userId) throws NotFoundException;

}
