package ru.practicum.exception.model;

import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static ru.practicum.DefaultDateTimeFormatter.getDefaultDateTimeFormatter;

@Getter
@RequiredArgsConstructor
@Generated
public class ErrorResponse {

    private final String timestamp = LocalDateTime.now().format(getDefaultDateTimeFormatter());

    private final String status;

    private final String error;

    private final String description;
}