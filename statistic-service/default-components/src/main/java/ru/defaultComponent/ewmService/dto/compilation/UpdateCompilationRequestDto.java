package ru.defaultComponent.ewmService.dto.compilation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequestDto {

    @UniqueElements
    List<Long> events;

    @Builder.Default
    Boolean pinned = Boolean.FALSE;

    @Size(min = 3, max = 50)
    String title;

}
