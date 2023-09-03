package ru.practicum.user.service;

import ru.defaultComponent.ewmServer.dto.user.CreateUserRequestDto;
import ru.defaultComponent.ewmServer.dto.user.UserResponseDto;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.user.model.UserEntity;
import java.util.List;

public interface UserAdminService {

    UserResponseDto createUser(CreateUserRequestDto createUserRequestDto);

    void deleteUser(long userId) throws NotFoundException;

    List<UserResponseDto> getUsersByIds(List<Long> userIds, int from, int size);

    UserEntity findUserEntityById(long userId) throws NotFoundException;

    void checkUserEntityIsExistById(long userId) throws NotFoundException;

}
