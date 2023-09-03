package ru.defaultComponent.ewmServer.dto.compilation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static java.lang.Boolean.FALSE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCompilationRequestDto {

    Long id;

    List<Long> events;

    @Builder.Default
    Boolean pinned = FALSE;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 50)
    String title;

}
