package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.defaultComponent.ewmService.dto.comment.CommentRequestUpdateStateDto;
import ru.defaultComponent.ewmService.dto.comment.CommentResponseDto;
import ru.defaultComponent.ewmService.dto.comment.CommentResponseUpdateStateDto;
import ru.defaultComponent.ewmService.dto.comment.UpdateCommentAdminRequestDto;
import ru.defaultComponent.ewmService.enums.CommentState;
import ru.practicum.comment.service.CommentAdminService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("admin/comments")
public class CommentAdminController {

    private final CommentAdminService commentAdminService;

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto getCommentById(@Positive @PathVariable long commentId) {
        log.info("EWM-SERVICE-admin => Запрошен комментарий по id => {}", commentId);
        return commentAdminService.getCommentById(commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponseDto> getAllComments(@RequestParam(required = false) List<Long> users,
                                                   @RequestParam(required = false) List<CommentState> states,
                                                   @RequestParam(required = false) List<Long> events,
                                                   @RequestParam(required = false) String rangeStart,
                                                   @RequestParam(required = false) String rangeEnd,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        log.info("EWM-SERVICE-admin => Запрошен поиск комментариев => " +
                        "users => {}, states => {}, events => {}, rangeStart => {}, rangeEnd => {}, from => {}, size => {}",
                users, states, events, rangeStart, rangeEnd, from, size);
        return commentAdminService.getAllComments(users, states, events, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto updateComment(@Positive @PathVariable long commentId,
                                            @Valid @RequestBody UpdateCommentAdminRequestDto updateCommentAdminRequestDto) {
        log.info("EWM-SERVICE-admin => Запрошено обновление комментария => commentId => {}", commentId);
        return commentAdminService.updateComment(commentId, updateCommentAdminRequestDto);
    }

    @PatchMapping()
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseUpdateStateDto changeStateComments(
            @Valid @RequestBody CommentRequestUpdateStateDto commentRequestUpdateStateDto) {
        log.info("EWM-SERVICE-admin => Запрошено обновление комментариев => commentIds => {}",
                commentRequestUpdateStateDto.getRequestIds());
        return commentAdminService.changeStateComments(commentRequestUpdateStateDto);
    }

}
