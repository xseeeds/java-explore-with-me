package ru.defaultComponent.exception.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Generated
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidationApiError {

    List<Violation> violations;

}