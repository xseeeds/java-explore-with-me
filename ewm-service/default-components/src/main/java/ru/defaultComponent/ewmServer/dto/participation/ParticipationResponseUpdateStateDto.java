package ru.defaultComponent.ewmServer.dto.participation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationResponseUpdateStateDto {

    List<ParticipationResponseDto> confirmedRequests;

    List<ParticipationResponseDto> rejectedRequests;

}
