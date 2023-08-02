package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.defaultComponent.ewmService.dto.user.NewUserRequest;
import ru.defaultComponent.ewmService.dto.user.UserDto;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.dao.UserRepository;

import java.util.List;

import static ru.defaultComponent.pageRequest.UtilPage.getPageSortAscByProperties;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserAdminService {

    private final UserRepository userRepository;

    @Transactional
    @Modifying
    @Override
    public UserDto createUser(NewUserRequest userRequest) {
        final UserDto userDto = UserMapper
                .toUserDto(
                userRepository.save(
                        UserMapper.toUserEntityFromRequest(userRequest)));
        log.info("ADMIN => Создан новый пользователь email => {}", userDto.getEmail());
        return userDto;
    }

    @Transactional
    @Modifying
    @Override
    public void deleteUser(long userId) throws NotFoundException {
        this.checkUserIsExistById(userId);
        userRepository.deleteById(userId);
        log.info("ADMIN => Пользователь удален по id => {}", userId);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        final Page<UserDto> userDtoPage = userRepository
                .findAllByAdmin(ids, getPageSortAscByProperties(from, size, "id"))
                .map(UserMapper::toUserDto);
        log.info("ADMIN => Пользователи получены size => {}", userDtoPage.getTotalElements());
        return userDtoPage.getContent();
    }

    @Override
    public UserEntity findUserEntityById(long userId) throws NotFoundException {
        log.info("ADMIN => Запрос пользователь по id => {} получен для СЕРВИСОВ", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        "ADMIN => Пользователь по id => " + userId + " не существует поиск СЕРВИСОВ"));
    }

    @Override
    public void checkUserIsExistById(long userId) throws NotFoundException {
        log.info("ADMIN => Запрос существует пользователь по id => {} для СЕРВИСОВ", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("ADMIN => Пользователь по id => " + userId + " не существует");
        }
    }
}
