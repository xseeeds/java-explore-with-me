package ru.defaultComponent.ewmService.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.defaultComponent.ewmService.dto.category.CategoryDto;
import ru.defaultComponent.ewmService.enums.EventState;
import ru.defaultComponent.ewmService.dto.user.UserShortDto;

import java.time.LocalDateTime;

import static ru.defaultComponent.dateTime.DefaultDateTimeFormatter.PATTERN_DATE_TIME;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {

    Long id;

    String annotation;

    CategoryDto category;

    Integer confirmedRequests;

    @JsonFormat(pattern = PATTERN_DATE_TIME)
    LocalDateTime createdOn;

    String description;

    @JsonFormat(pattern = PATTERN_DATE_TIME)
    LocalDateTime eventDate;

    UserShortDto initiator;

    LocationDto location;

    Boolean paid;

    Integer participantLimit;

    @JsonFormat(pattern = PATTERN_DATE_TIME)
    LocalDateTime publishedOn;

    Boolean requestModeration;

    EventState state;

    String title;

    Integer views;

}
