package ru.defaultComponent.ewmService.dto.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.defaultComponent.ewmService.enums.RequestUserState;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCommentUserRequestDto {

    @Size(min = 5, max = 5000)
    String text;

    RequestUserState stateAction;

}