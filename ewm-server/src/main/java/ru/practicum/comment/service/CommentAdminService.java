package ru.practicum.comment.service;

import ru.defaultComponent.ewmServer.dto.comment.CommentRequestUpdateStateDto;
import ru.defaultComponent.ewmServer.dto.comment.CommentResponseDto;
import ru.defaultComponent.ewmServer.dto.comment.CommentResponseUpdateStateDto;
import ru.defaultComponent.ewmServer.dto.comment.UpdateCommentAdminRequestDto;
import ru.defaultComponent.ewmServer.enums.CommentState;
import ru.defaultComponent.exception.exp.BadRequestException;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.comment.model.CommentEntity;

import java.util.List;

public interface CommentAdminService {

    CommentResponseDto getCommentById(long commentId) throws NotFoundException;

    List<CommentResponseDto> getAllComments(List<Long> users,
                                            List<CommentState> states,
                                            List<Long> events,
                                            String rangeStart,
                                            String rangeEnd,
                                            int from,
                                            int size);

    CommentResponseDto updateComment(long commentId, UpdateCommentAdminRequestDto updateCommentAdminRequestDto)
            throws BadRequestException, NotFoundException, ConflictException;

    CommentResponseUpdateStateDto changeStateComments(CommentRequestUpdateStateDto commentRequestUpdateStateDto);

    CommentEntity findCommentEntityById(long commentId) throws NotFoundException;

    void checkCommentEntityIsExist(long commentId) throws NotFoundException;

    CommentEntity findByIdAndAuthor(long commentId, long userId) throws ConflictException;

    void checkCommentEntityIsExistByUserId(long commentId, long userId) throws ConflictException;

}
