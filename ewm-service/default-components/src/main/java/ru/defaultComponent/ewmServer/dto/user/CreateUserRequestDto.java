package ru.defaultComponent.ewmServer.dto.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequestDto {

    Long id;

    @Email
    @NotNull
    @NotBlank
    @Size(min = 6, max = 254)
    String email;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 250)
    String name;

}
