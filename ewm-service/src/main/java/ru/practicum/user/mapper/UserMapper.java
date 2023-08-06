package ru.practicum.user.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.ewmService.dto.user.CreateUserRequest;
import ru.defaultComponent.ewmService.dto.user.UserResponseDto;
import ru.defaultComponent.ewmService.dto.user.UserShortResponseDto;
import ru.practicum.user.model.UserEntity;

@UtilityClass
public class UserMapper {

    public UserEntity toUserEntity(CreateUserRequest createUserRequest) {
        return UserEntity
                .builder()
                .id(createUserRequest.getId())
                .name(createUserRequest.getName())
                .email(createUserRequest.getEmail())
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
