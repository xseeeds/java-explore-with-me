package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.defaultComponent.ewmServer.dto.user.CreateUserRequestDto;
import ru.defaultComponent.ewmServer.dto.user.UserResponseDto;
import ru.practicum.user.service.UserAdminService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserAdminController {

    private final UserAdminService userAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody @Valid CreateUserRequestDto createUserRequestDto) {
        log.info("EWM-SERVER-admin => Запрос создание пользователя email => {}", createUserRequestDto.getEmail());
        return userAdminService.createUser(createUserRequestDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Positive @PathVariable long userId) {
        log.info("EWM-SERVER-admin => Запрос удаление пользователя id => {}", userId);
        userAdminService.deleteUser(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getUsersByIds(@RequestParam(name = "ids", required = false) List<Long> userIds,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        log.info("EWM-SERVER-admin => Запрошено получение пользователей по userIds => {}, from => {}, size => {}",
                userIds, from, size);
        return userAdminService.getUsersByIds(userIds, from, size);
    }

}
