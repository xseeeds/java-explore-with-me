package ru.defaultComponent.ewmServer.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.defaultComponent.ewmServer.dto.event.EventShortResponseDto;
import ru.defaultComponent.ewmServer.dto.user.UserShortResponseDto;
import ru.defaultComponent.ewmServer.enums.CommentState;

import java.time.LocalDateTime;

import static ru.defaultComponent.dateTime.DefaultDateTimeFormatter.PATTERN_DATE_TIME;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponseDto {

    Long id;

    UserShortResponseDto author;

    EventShortResponseDto event;

    @JsonFormat(pattern = PATTERN_DATE_TIME)
    LocalDateTime createdOn;

    @JsonFormat(pattern = PATTERN_DATE_TIME)
    LocalDateTime publishedOn;

    CommentState state;

    String text;

}