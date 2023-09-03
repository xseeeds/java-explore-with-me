package ru.defaultComponent.ewmServer.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.defaultComponent.ewmServer.enums.RequestAdminState;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.defaultComponent.dateTime.DefaultDateTimeFormatter.PATTERN_DATE_TIME;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequestDto {

    @Size(min = 20, max = 2000)
    String annotation;

    Long category;

    @Size(min = 20, max = 7000)
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE_TIME)
    LocalDateTime eventDate;

    LocationRequestDto location;

    Boolean paid;

    Long participantLimit;

    Boolean requestModeration;

    RequestAdminState stateAction;

    @Size(min = 3, max = 120)
    String title;

}
