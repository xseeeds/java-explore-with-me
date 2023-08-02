package ru.practicum.exceptionHandler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.defaultComponent.exception.model.ApiError;
import ru.defaultComponent.exception.exp.BadRequestException;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.defaultComponent.exception.model.ValidationErrorResponse;
import ru.defaultComponent.exception.model.Violation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice("ru.practicum")
@Generated
public class ErrorHandler {

    private final LocalDateTime now = LocalDateTime.now();

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ApiError errorBadRequestException(
            final BadRequestException e
    ) {
        log.warn("EVENT-SERVER => " + e.getMessage(), e);
        return ApiError
                .builder()
                .timestamp(now)
                .status(BAD_REQUEST)
                .reason("EVENT-SERVER => errorBadRequestException")
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MismatchedInputException.class)
    public ApiError errorMismatchedInputException(
            final MismatchedInputException e
    ) {
        log.warn("EVENT-SERVER => " + e.getMessage(), e);
        final int endIndex = nthIndexOf(e.getMessage(), "\n", 1);
        return ApiError
                .builder()
                .timestamp(now)
                .status(BAD_REQUEST)
                .reason("EVENT-SERVER => errorMismatchedInputException")
                .message(e.getMessage().substring(0, endIndex))
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiError errorMissingServletRequestParameterException(
            final MissingServletRequestParameterException e
    ) {
        log.warn("EVENT-SERVER => " + e.getMessage(), e);
        return ApiError
                .builder()
                .timestamp(now)
                .status(BAD_REQUEST)
                .reason("EVENT-SERVER => errorMissingServletRequestParameterException")
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ApiError errorMissingRequestHeaderException(
            final MissingRequestHeaderException e
    ) {
        log.warn("EVENT-SERVER => " + e.getMessage(), e);
        return ApiError
                .builder()
                .timestamp(now)
                .status(BAD_REQUEST)
                .reason("EVENT-SERVER => errorMissingRequestHeaderException")
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ValidationErrorResponse errorConstraintValidationException(
            final ConstraintViolationException e
    ) {
        log.warn("EVENT-SERVER => " + e.getMessage(), e);
        final List<Violation> errorPathVariable = e.getConstraintViolations()
                .stream()
                .map(error -> Violation
                        .builder()
                        .timestamp(now)
                        .status(BAD_REQUEST)
                        .reason("EVENT-SERVER => errorConstraintValidationException")
                        .fieldName(error.getPropertyPath().toString())
                        .message(error.getMessage())
                        .build())
                .collect(toList());
        return ValidationErrorResponse
                .builder()
                .violations(errorPathVariable)
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorResponse errorMethodArgumentNotValidException(
            final MethodArgumentNotValidException e
    ) {
        log.warn("EVENT-SERVER => " + e.getMessage(), e);
        final List<Violation> errorRequestBody = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Violation
                        .builder()
                        .timestamp(now)
                        .status(BAD_REQUEST)
                        .reason("EVENT-SERVER => errorMethodArgumentNotValidException")
                        .fieldName(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .collect(toList());
        return ValidationErrorResponse
                .builder()
                .violations(errorRequestBody)
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ApiError errorNotFoundException(
            final NotFoundException e
    ) {
        log.warn("EVENT-SERVER => " + e.getMessage(), e);
        return ApiError
                .builder()
                .timestamp(now)
                .status(NOT_FOUND)
                .reason("EVENT-SERVER => errorNotFoundException")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(CONFLICT)
    public ApiError errorDataIntegrityViolationException(
            final DataIntegrityViolationException e
    ) {
        log.warn("EVENT-SERVER => " + e.getMessage(), e);
        final int endIndex = nthIndexOf(e.getMostSpecificCause().getMessage(), ")", 2);
        return ApiError
                .builder()
                .timestamp(now)
                .status(CONFLICT)
                .reason("EVENT-SERVER => errorDataIntegrityViolationException")
                .message(e.getMostSpecificCause().getMessage().substring(0, endIndex + 1))
                .build();
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ApiError errorConflictException(
            final ConflictException e
    ) {
        log.warn("EVENT-SERVER => " + e.getMessage(), e);
        return ApiError
                .builder()
                .timestamp(now)
                .status(CONFLICT)
                .reason("EVENT-SERVER => errorConflictException")
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ApiError errorSQLIntegrityConstraintViolationException(
            final SQLIntegrityConstraintViolationException e
    ) {
        log.warn("EVENT-SERVER => " + e.getMessage(), e);
        final int endIndex = nthIndexOf(e.getMessage(), ")", 2);
        return ApiError
                .builder()
                .timestamp(now)
                .status(CONFLICT)
                .reason("EVENT-SERVER => errorSQLIntegrityConstraintViolationException")
                .message(e.getMessage().substring(0, endIndex + 1))
                .build();
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ApiError errorSQLSyntaxErrorException(
            final SQLSyntaxErrorException e
    ) {
        log.warn("EVENT-SERVER => " + e.getMessage(), e);
        final int endIndex = nthIndexOf(e.getMessage(), "\n", 1);
        return ApiError
                .builder()
                .timestamp(now)
                .status(CONFLICT)
                .reason("EVENT-SERVER => errorSQLSyntaxErrorException")
                .message(e.getMessage().substring(0, endIndex))
                .build();
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ApiError errorInternalServerErrorException(final Throwable e) {
        log.warn("EVENT-SERVER => " + e.getMessage(), e);
        return ApiError
                .builder()
                .timestamp(now)
                .status(INTERNAL_SERVER_ERROR)
                .reason("EVENT-SERVER => errorInternalServerErrorException")
                .message("Произошла непредвиденная ошибка => " + e.getMessage())
                .build();
    }


    private int nthIndexOf(String str, String substr, int nth) {
        int pos = str.indexOf(substr);
        while (--nth > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

}
