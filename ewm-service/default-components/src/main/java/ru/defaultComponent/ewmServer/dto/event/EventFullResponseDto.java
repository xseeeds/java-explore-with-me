package ru.defaultComponent.ewmServer.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.defaultComponent.ewmServer.dto.category.CategoryResponseDto;
import ru.defaultComponent.ewmServer.enums.EventState;
import ru.defaultComponent.ewmServer.dto.user.UserShortResponseDto;
import java.time.LocalDateTime;

import static ru.defaultComponent.dateTime.DefaultDateTimeFormatter.PATTERN_DATE_TIME;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullResponseDto {

    Long id;

    String annotation;

    CategoryResponseDto category;

    Long confirmedRequests;

    @JsonFormat(pattern = PATTERN_DATE_TIME)
    LocalDateTime createdOn;

    String description;

    @JsonFormat(pattern = PATTERN_DATE_TIME)
    LocalDateTime eventDate;

    UserShortResponseDto initiator;

    LocationResponseDto location;

    Boolean paid;

    Long participantLimit;

    @JsonFormat(pattern = PATTERN_DATE_TIME)
    LocalDateTime publishedOn;

    Boolean requestModeration;

    EventState state;

    String title;

    Long views;

}
