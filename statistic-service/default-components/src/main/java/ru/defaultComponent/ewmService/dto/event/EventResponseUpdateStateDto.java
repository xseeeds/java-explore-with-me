package ru.defaultComponent.ewmService.dto.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.defaultComponent.ewmService.dto.request.ParticipationResponseDto;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventResponseUpdateStateDto {

    List<ParticipationResponseDto> confirmedRequests;

    List<ParticipationResponseDto> rejectedRequests;

}
