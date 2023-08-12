package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.defaultComponent.ewmService.dto.comment.*;
import ru.defaultComponent.ewmService.enums.EventState;
import ru.defaultComponent.exception.exp.BadRequestException;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.comment.mapper.CommentMapper;
import ru.defaultComponent.ewmService.enums.CommentState;
import ru.practicum.comment.dao.CommentRepository;
import ru.practicum.comment.model.CommentEntity;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.service.EventAdminService;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.service.UserAdminService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static ru.defaultComponent.dateTime.DefaultDateTimeFormatter.getLocalDateTimeFormatting;
import static ru.defaultComponent.dateTime.CheckLocalDateTime.checkStartIsAfterEndMayBeNull;
import static ru.defaultComponent.pageRequest.UtilPage.getPageSortDescByProperties;
import static ru.defaultComponent.ewmService.enums.RequestAdminState.*;
import static ru.defaultComponent.ewmService.enums.RequestUserState.*;
import static ru.defaultComponent.ewmService.enums.CommentState.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentAdminService, CommentPrivateService, CommentPublicService {

    private final CommentRepository commentRepository;
    private final UserAdminService userAdminService;
    private final EventAdminService eventAdminService;

    @Override
    public CommentResponseDto getCommentById(long commentId) throws NotFoundException {
        final CommentEntity commentEntity = this.findCommentEntityById(commentId);
        final CommentResponseDto commentResponseDto = CommentMapper.toCommentResponseDto(commentEntity);
        log.info("ADMIN => Получен комментарий по id => {}", commentId);
        return commentResponseDto;
    }

    @Override
    public List<CommentResponseDto> getAllComments(List<Long> users, List<CommentState> states, List<Long> events,
                                                   String rangeStart, String rangeEnd, int from, int size) {
        final LocalDateTime start = getLocalDateTimeFormatting(rangeStart);
        final LocalDateTime end = getLocalDateTimeFormatting(rangeEnd);
        checkStartIsAfterEndMayBeNull(start, end);
        final Page<CommentResponseDto> commentResponseDtoPage = commentRepository.findByAdmin(
                        users, states, events, start, end,
                        getPageSortDescByProperties(from, size, "createdOn"))
                .map(CommentMapper::toCommentResponseDto);
        log.info("ADMIN => Поиск комментариев => totalElements => {} " +
                        "users => {}, states => {}, events => {}, rangeStart => {}, rangeEnd => {}, from => {}, size => {}",
                commentResponseDtoPage.getTotalElements(), users, states, events, rangeStart, rangeEnd, from, size);
        return commentResponseDtoPage.getContent();
    }

    @Transactional
    @Modifying
    @Override
    public CommentResponseDto updateComment(long commentId, UpdateCommentAdminRequestDto updateCommentAdminRequestDto)
            throws BadRequestException, NotFoundException, ConflictException {
        final CommentEntity commentEntity = this.findCommentEntityById(commentId);
        if (commentEntity.getState() != PENDING && updateCommentAdminRequestDto.getStateAction() == PUBLISH_EVENT) {
            throw new BadRequestException("ADMIN => Комментарий по id => " + commentId + " уже рассмотрен");
        }
        if (updateCommentAdminRequestDto.getStateAction() == REJECT_EVENT) {
            commentEntity.setState(CANCELED);
        }
        if (updateCommentAdminRequestDto.getStateAction() == PUBLISH_EVENT) {
            commentEntity.setPublishedOn(LocalDateTime.now());
            commentEntity.setState(PUBLISHED);
        }
        final CommentResponseDto commentResponseDto = CommentMapper
                .toCommentResponseDto(
                        commentRepository.save(commentEntity));
        log.info("ADMIN => Комментария по id => {} обновлен", commentId);
        return commentResponseDto;
    }

    @Override
    public CommentResponseUpdateStateDto changeStateComments(CommentRequestUpdateStateDto commentRequestUpdateStateDto) {
        log.info("ADMIN => Запрошено обновление комментариев => commentIds => {}",
                commentRequestUpdateStateDto.getRequestIds());
        throw new ResponseStatusException(NOT_IMPLEMENTED, "Метод /changeStateComments не реализован.");
    }

    @Override
    public CommentEntity findCommentEntityById(long commentId) throws NotFoundException {
        log.info("ADMIN => Запрос комментария по id => {}", commentId);
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(
                        "ADMIN => Комментарий по id => " + commentId + " не существует"));
    }

    @Override
    public void checkCommentEntityIsExist(long commentId) throws NotFoundException {
        log.info("ADMIN => Запрос существует комментарий по id => {}", commentId);
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("ADMIN => Комментарий по id => " + commentId + " не существует");
        }
    }

    @Override
    public CommentEntity findByIdAndAuthor(long commentId, long userId) throws ConflictException {
        log.info("ADMIN => Запрос комментария по id => {} и принадлежащего пользователю по id => {}",
                commentId, userId);
        return commentRepository.findByIdAndAuthor(commentId, userId)
                .orElseThrow(() -> new ConflictException("ADMIN => Комментарий по id => " + commentId
                        + " не принадлежит пользователю по id => " + userId));
    }

    @Override
    public void checkCommentEntityIsExistByUserId(long commentId, long userId) throws ConflictException {
        log.info("ADMIN => Запрос принадлежит комментарий по id => {} пользователю по id => {}",
                commentId, userId);
        if (!commentRepository.existsByIdAndAuthor(commentId, userId)) {
            throw new ConflictException("ADMIN => Комментарий по id => " + commentId
                    + " не принадлежит пользователю по id => " + userId);
        }
    }

    @Transactional
    @Modifying
    @Override
    public CommentResponseDto createComment(long eventId, long userId, CreateCommentRequestDto createCommentRequestDto)
            throws NotFoundException, ConflictException {
        final EventEntity eventEntity = eventAdminService.findEventEntityById(eventId);
        if (eventEntity.getState() != EventState.PUBLISHED) {
            throw new ConflictException("PRIVATE => Событие ещё не опубликовано");
        }
        final UserEntity userEntity = userAdminService.findUserEntityById(userId);
        final CommentResponseDto commentResponseDto = CommentMapper
                .toCommentResponseDto(
                        commentRepository.save(CommentMapper
                                .toNewCommentEntity(createCommentRequestDto, userEntity, eventEntity)));
        log.info("PRIVATE => Добавлен новый комментарий => {}, пользователем по id => {}, " +
                "к событию по id => {}", commentResponseDto, userId, eventId);
        return commentResponseDto;
    }

    @Override
    public CommentResponseDto getCommentByIdAndUserId(long commentId, long userId) throws ConflictException {
        final CommentResponseDto commentResponseDto = CommentMapper
                .toCommentResponseDto(
                        this.findByIdAndAuthor(commentId, userId));
        log.info("PRIVATE => Получен комментарий по id => {}, принадлежащий пользователю по id => {}", commentId, userId);
        return commentResponseDto;
    }

    @Transactional
    @Modifying
    @Override
    public CommentResponseDto updateComment(long commentId, long userId,
                                            UpdateCommentUserRequestDto updateCommentUserRequestDto)
            throws ConflictException {
        userAdminService.checkUserEntityIsExistById(userId);
        final CommentEntity commentEntity = this.findByIdAndAuthor(commentId, userId);
        if (updateCommentUserRequestDto.getStateAction() != null) {
            if (updateCommentUserRequestDto.getStateAction() == CANCEL_REVIEW) {
                commentEntity.setState(CANCELED);
            }
            if (updateCommentUserRequestDto.getStateAction() == SEND_TO_REVIEW) {
                commentEntity.setState(PENDING);
            }
        }
        if (updateCommentUserRequestDto.getText() != null) {
            commentEntity.setText(updateCommentUserRequestDto.getText());
            commentEntity.setState(PENDING);
        }
        final CommentResponseDto commentResponseDto = CommentMapper
                .toCommentResponseDto(
                        commentRepository.save(commentEntity));
        log.info("PRIVATE => Обновлен комментарий по id => {}, пользователем по id => {}", commentId, userId);
        return commentResponseDto;
    }

    @Transactional
    @Modifying
    @Override
    public void deleteComment(long commentId, long userId) throws NotFoundException {
        this.checkCommentEntityIsExistByUserId(commentId, userId);
        commentRepository.deleteById(commentId);
        log.info("PRIVATE => Удален комментарий по id => {} пользователем по id => {}", commentId, userId);
    }

    @Override
    public List<CommentResponseDto> getAllComments(int from, int size) {
        final Page<CommentResponseDto> commentResponseDtoPage = commentRepository.findAllByState(
                        PUBLISHED, getPageSortDescByProperties(from, size, "publishedOn"))
                .map(CommentMapper::toCommentResponseDto);
        log.info("PUBLIC => Запрошены все комментарии totalElements => {}, from => {}, size => {}",
                commentResponseDtoPage.getTotalElements(), from, size);
        return commentResponseDtoPage.getContent();
    }

    @Override
    public List<CommentResponseDto> getCommentsByEventId(long eventId, int from, int size) {
        eventAdminService.checkEventEntityIsExistById(eventId);
        final Page<CommentResponseDto> commentResponseDtoPage = commentRepository.findCommentsByEventAndState(
                        eventId, PUBLISHED,
                        getPageSortDescByProperties(from, size, "publishedOn"))
                .map(CommentMapper::toCommentResponseDto);
        log.info("PUBLIC => Запрошены все комментарии totalElements => {}, события по id => {}, from => {}, size => {}",
                commentResponseDtoPage.getTotalElements(), eventId, from, size);
        return commentResponseDtoPage.getContent();
    }

}