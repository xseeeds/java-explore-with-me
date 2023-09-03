package ru.defaultComponent.ewmServer.dto.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.defaultComponent.ewmServer.dto.participation.ParticipationResponseDto;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponseUpdateStateDto {

    List<ParticipationResponseDto> confirmedRequests;

    List<ParticipationResponseDto> rejectedRequests;

}
