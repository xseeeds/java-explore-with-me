package ru.practicum.user.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.ewmService.dto.user.CreateUserRequestDto;
import ru.defaultComponent.ewmService.dto.user.UserResponseDto;
import ru.defaultComponent.ewmService.dto.user.UserShortResponseDto;
import ru.practicum.user.model.UserEntity;

@UtilityClass
public class UserMapper {

    public UserEntity toUserEntity(CreateUserRequestDto createUserRequestDto) {
        return UserEntity
                .builder()
                .id(createUserRequestDto.getId())
                .name(createUserRequestDto.getName())
                .email(createUserRequestDto.getEmail())
                .build();
    }

    public UserResponseDto toUserResponseDto(UserEntity useEntity) {
        return UserResponseDto
                .builder()
                .id(useEntity.getId())
                .name(useEntity.getName())
                .email(useEntity.getEmail())
                .build();
    }

    public UserShortResponseDto toUserShortResponseDto(UserEntity userEntity) {
        return UserShortResponseDto
                .builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .build();
    }

}
