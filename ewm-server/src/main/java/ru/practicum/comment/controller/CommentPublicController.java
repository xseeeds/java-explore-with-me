package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.defaultComponent.ewmServer.dto.comment.CommentResponseDto;
import ru.practicum.comment.service.CommentPublicService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentPublicController {

    private final CommentPublicService commentPublicService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<CommentResponseDto> getAllComments(@RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        log.info("EWM-SERVER-public => Запрошены все комментарии from => {}, size => {}", from, size);
        return commentPublicService.getAllComments(from, size);
    }

    @GetMapping("/event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    List<CommentResponseDto> getCommentsByEventId(@Positive @PathVariable long eventId,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        log.info("EWM-SERVER-public => Запрошены все комментарии события по id => {}, from => {}, size => {}",
                eventId, from, size);
        return commentPublicService.getCommentsByEventId(eventId, from, size);
    }

}