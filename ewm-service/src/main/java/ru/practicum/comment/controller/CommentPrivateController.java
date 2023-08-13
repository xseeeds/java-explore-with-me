package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.defaultComponent.ewmService.dto.comment.CommentResponseDto;
import ru.defaultComponent.ewmService.dto.comment.CreateCommentRequestDto;
import ru.defaultComponent.ewmService.dto.comment.UpdateCommentUserRequestDto;
import ru.practicum.comment.service.CommentPrivateService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "users/{userId}/comments")
public class CommentPrivateController {

    private final CommentPrivateService commentPrivateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@Positive @PathVariable long userId,
                                            @Positive @RequestParam long eventId,
                                            @Valid @RequestBody CreateCommentRequestDto createCommentRequestDto) {
        log.info("EWM-SERVICE-private => Запрошено добавление нового комментария => {}, пользователем по id => {}, " +
                "к событию по id => {}", createCommentRequestDto, userId, eventId);
        return commentPrivateService.createComment(eventId, userId, createCommentRequestDto);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto getCommentByIdAndUserId(@Positive @PathVariable long userId,
                                                      @Positive @PathVariable long commentId) {
        log.info("EWM-SERVICE-private => Запрошен комментарий по id => {}, пользователем по id => {}", commentId, userId);
        return commentPrivateService.getCommentByIdAndUserId(commentId, userId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto updateComment(@Positive @PathVariable long userId,
                                            @Positive @PathVariable long commentId,
                                            @Valid @RequestBody UpdateCommentUserRequestDto updateCommentUserRequestDto) {
        log.info("EWM-SERVICE-private => Запрошено обновление комментария по id => {}, пользователем по id => {}",
                commentId, userId);
        return commentPrivateService.updateComment(commentId, userId, updateCommentUserRequestDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@Positive @PathVariable long commentId,
                              @Positive @PathVariable long userId) {
        log.info("EWM-SERVICE-private => Запрошено удаление комментария по id => {}, пользователем по id => {}",
                commentId, userId);
        commentPrivateService.deleteComment(commentId, userId);
    }

}