package ru.practicum.user.service;

import ru.defaultComponent.ewmService.dto.user.CreateUserRequest;
import ru.defaultComponent.ewmService.dto.user.UserResponseDto;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.user.model.UserEntity;

import java.util.List;

public interface UserAdminService {

    UserResponseDto createUser(CreateUserRequest userRequest);

    void deleteUser(long userId) throws NotFoundException;

    List<UserResponseDto> getUsers(List<Long> ids, int from, int size);

    UserEntity findUserEntityById(long userId) throws NotFoundException;

    void checkUserIsExistById(long userId) throws NotFoundException;

}
