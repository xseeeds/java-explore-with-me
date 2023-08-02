package ru.server.exceptionHandler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.defaultComponent.exception.exp.BadRequestException;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.defaultComponent.exception.model.ErrorResponse;
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
@RestControllerAdvice("ru.server")
@Generated
public class ErrorHandler {

    private final LocalDateTime now = LocalDateTime.now();

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponse errorBadRequestException(
            final BadRequestException e
    ) {
        log.warn("STATISTIC-SERVER => " + e.getMessage(), e);
        return ErrorResponse
                .builder()
                .timestamp(now)
                .status(BAD_REQUEST)
                .reason("errorBadRequestException")
                .description(e.getMessage())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MismatchedInputException.class)
    public ErrorResponse errorMismatchedInputException(
            final MismatchedInputException e
    ) {
        log.warn("STATISTIC-SERVER => " + e.getMessage(), e);
        final int endIndex = nthIndexOf(e.getMessage(), "\n", 1);
        return ErrorResponse
                .builder()
                .timestamp(now)
                .status(BAD_REQUEST)
                .reason("errorMismatchedInputException")
                .description(e.getMessage().substring(0, endIndex))
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResponse errorMissingServletRequestParameterException(
            final MissingServletRequestParameterException e
    ) {
        log.warn("STATISTIC-SERVER => " + e.getMessage(), e);
        return ErrorResponse
                .builder()
                .timestamp(now)
                .status(BAD_REQUEST)
                .reason("errorMissingServletRequestParameterException")
                .description(e.getMessage())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ErrorResponse errorMissingRequestHeaderException(
            final MissingRequestHeaderException e
    ) {
        log.warn("STATISTIC-SERVER => " + e.getMessage(), e);
        return ErrorResponse
                .builder()
                .timestamp(now)
                .status(BAD_REQUEST)
                .reason("STATISTIC-SERVER => errorMissingRequestHeaderException")
                .description(e.getMessage())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ValidationErrorResponse errorConstraintValidationException(
            final ConstraintViolationException e
    ) {
        log.warn("STATISTIC-SERVER => " + e.getMessage(), e);
        final List<Violation> errorPathVariable = e.getConstraintViolations()
                .stream()
                .map(error -> Violation
                        .builder()
                        .timestamp(now)
                        .status(BAD_REQUEST)
                        .reason("STATISTIC-SERVER => errorConstraintValidationException")
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
        log.warn("STATISTIC-SERVER => " + e.getMessage(), e);
        final List<Violation> errorRequestBody = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Violation
                        .builder()
                        .timestamp(now)
                        .status(BAD_REQUEST)
                        .reason("STATISTIC-SERVER => errorMethodArgumentNotValidException")
                        .fieldName(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .collect(toList());
        return ValidationErrorResponse
                .builder()
                .violations(errorRequestBody)
                .build();
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse errorNotFoundException(
            final NotFoundException e
    ) {
        log.warn("STATISTIC-SERVER => " + e.getMessage(), e);
        return ErrorResponse
                .builder()
                .timestamp(now)
                .status(NOT_FOUND)
                .reason("STATISTIC-SERVER => errorNotFoundException")
                .description(e.getMessage())
                .build();
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ErrorResponse errorConflictException(
            final ConflictException e
    ) {
        log.warn("STATISTIC-SERVER => " + e.getMessage(), e);
        return ErrorResponse
                .builder()
                .timestamp(now)
                .status(CONFLICT)
                .reason("STATISTIC-SERVER => errorConflictException")
                .description(e.getMessage())
                .build();
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse errorDataIntegrityViolationException(
            final DataIntegrityViolationException e
    ) {
        log.warn("STATISTIC-SERVER => " + e.getMessage(), e);
        final int endIndex = nthIndexOf(e.getMostSpecificCause().getMessage(), ")", 2);
        return ErrorResponse
                .builder()
                .timestamp(now)
                .status(CONFLICT)
                .reason("STATISTIC-SERVER => errorDataIntegrityViolationException")
                .description(e.getMostSpecificCause().getMessage().substring(0, endIndex + 1))
                .build();
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ErrorResponse errorSQLIntegrityConstraintViolationException(
            final SQLIntegrityConstraintViolationException e
    ) {
        log.warn("STATISTIC-SERVER => " + e.getMessage(), e);
        final int endIndex = nthIndexOf(e.getMessage(), ")", 2);
        return ErrorResponse
                .builder()
                .timestamp(now)
                .status(CONFLICT)
                .reason("STATISTIC-SERVER => errorSQLIntegrityConstraintViolationException")
                .description(e.getMessage().substring(0, endIndex + 1))
                .build();
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ErrorResponse errorSQLSyntaxErrorException(
            final SQLSyntaxErrorException e
    ) {
        log.warn("STATISTIC-SERVER => " + e.getMessage(), e);
        final int endIndex = nthIndexOf(e.getMessage(), "\n", 1);
        return ErrorResponse
                .builder()
                .timestamp(now)
                .status(CONFLICT)
                .reason("STATISTIC-SERVER => errorSQLSyntaxErrorException")
                .description(e.getMessage().substring(0, endIndex))
                .build();
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse errorInternalServerErrorException(final Throwable e) {
        log.warn("STATISTIC-SERVER => " + e.getMessage(), e);
        return ErrorResponse
                .builder()
                .timestamp(now)
                .status(INTERNAL_SERVER_ERROR)
                .reason("STATISTIC-SERVER => errorInternalServerErrorException")
                .description("Произошла непредвиденная ошибка => " + e.getMessage())
                .build();
    }


    private int nthIndexOf(String str, String substr, int nth) {
        int pos = str.indexOf(substr);
        while (--nth > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

}
