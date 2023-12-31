package ru.defaultComponent.ewmServer.dto.participation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.defaultComponent.ewmServer.enums.RequestState;
import java.time.LocalDateTime;

import static ru.defaultComponent.dateTime.DefaultDateTimeFormatter.PATTERN_DATE_TIME;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationResponseDto {

    Long id;

    @JsonFormat(pattern = PATTERN_DATE_TIME)
    LocalDateTime created;

    Long event;

    Long requester;

    RequestState status;

}
