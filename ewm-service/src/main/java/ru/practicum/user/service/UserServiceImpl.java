package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.defaultComponent.ewmService.dto.user.CreateUserRequestDto;
import ru.defaultComponent.ewmService.dto.user.UserResponseDto;
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
    public UserResponseDto createUser(CreateUserRequestDto createUserRequestDto) {
        final UserResponseDto userResponseDto = UserMapper
                .toUserResponseDto(
                userRepository.save(
                        UserMapper.toUserEntity(createUserRequestDto)));
        log.info("ADMIN => Создан новый пользователь email => {}", userResponseDto.getEmail());
        return userResponseDto;
    }

    @Transactional
    @Modifying
    @Override
    public void deleteUser(long userId) throws NotFoundException {
        this.checkUserEntityIsExistById(userId);
        userRepository.deleteById(userId);
        log.info("ADMIN => Пользователь удален по id => {}", userId);
    }

    @Override
    public List<UserResponseDto> getUsersByIds(List<Long> userIds, int from, int size) {
        final Page<UserResponseDto> userDtoPage = userRepository
                .findAllByIdIn(userIds, getPageSortAscByProperties(from, size, "id"))
                .map(UserMapper::toUserResponseDto);
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
    public void checkUserEntityIsExistById(long userId) throws NotFoundException {
        log.info("ADMIN => Запрос существует пользователь по id => {} для СЕРВИСОВ", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("ADMIN => Пользователь по id => " + userId + " не существует поиск СЕРВИСОВ");
        }
    }
}
