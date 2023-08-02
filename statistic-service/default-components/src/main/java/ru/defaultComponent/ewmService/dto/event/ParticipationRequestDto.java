package ru.defaultComponent.ewmService.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.defaultComponent.ewmService.enums.RequestStatus;

import java.time.LocalDateTime;

import static ru.defaultComponent.dateTime.DefaultDateTimeFormatter.PATTERN_DATE_TIME;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {

    Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE_TIME)
    LocalDateTime created;

    Long event;

    Long requester;

    RequestStatus status;

}
