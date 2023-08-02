package ru.practicum.user.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.ewmService.dto.user.NewUserRequest;
import ru.defaultComponent.ewmService.dto.user.UserDto;
import ru.defaultComponent.ewmService.dto.user.UserShortDto;
import ru.practicum.user.model.UserEntity;

@UtilityClass
public class UserMapper {

    public UserEntity toUserEntityFromRequest(NewUserRequest newUserRequest) {
        return UserEntity
                .builder()
                .id(newUserRequest.getId())
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public UserDto toUserDto(UserEntity useEntity) {
        return UserDto
                .builder()
                .id(useEntity.getId())
                .name(useEntity.getName())
                .email(useEntity.getEmail())
                .build();
    }

    public UserShortDto toUserShortDto(UserEntity userEntity) {
        return UserShortDto
                .builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .build();
    }

}
