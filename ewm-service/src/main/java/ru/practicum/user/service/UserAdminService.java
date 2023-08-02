package ru.practicum.user.service;

import ru.defaultComponent.ewmService.dto.user.NewUserRequest;
import ru.defaultComponent.ewmService.dto.user.UserDto;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.user.model.UserEntity;

import java.util.List;

public interface UserAdminService {

    UserDto createUser(NewUserRequest userRequest);

    void deleteUser(long userId) throws NotFoundException;

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserEntity findUserEntityById(long userId) throws NotFoundException;

    void checkUserIsExistById(long userId) throws NotFoundException;

}
