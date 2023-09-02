package ru.defaultComponent.ewmService.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.defaultComponent.ewmService.dto.category.CategoryResponseDto;
import ru.defaultComponent.ewmService.dto.user.UserShortResponseDto;
import java.time.LocalDateTime;

import static ru.defaultComponent.dateTime.DefaultDateTimeFormatter.PATTERN_DATE_TIME;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortResponseDto {

    Long id;

    String annotation;

    CategoryResponseDto category;

    Long confirmedRequests;

    @JsonFormat(pattern = PATTERN_DATE_TIME)
    LocalDateTime eventDate;

    UserShortResponseDto initiator;

    Boolean paid;

    String title;

    Long views;

}
