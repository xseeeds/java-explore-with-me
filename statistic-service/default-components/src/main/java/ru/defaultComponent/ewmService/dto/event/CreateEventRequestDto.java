package ru.defaultComponent.ewmService.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.LocalDateTime;

import static ru.defaultComponent.dateTime.DefaultDateTimeFormatter.PATTERN_DATE_TIME;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateEventRequestDto {

    @NotNull
    @Size(min = 20, max = 2000)
    String annotation;

    @NotNull
    Long category;

    @NotNull
    @Size(min = 20, max = 7000)
    String description;

    @NotNull
    LocationRequestDto location;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE_TIME)
    LocalDateTime eventDate;

    @Builder.Default
    Boolean paid = Boolean.FALSE;

    Integer participantLimit = 0;

    @Builder.Default
    Boolean requestModeration = Boolean.TRUE;

    @NotNull
    @Size(min = 3, max = 120)
    String title;

}
