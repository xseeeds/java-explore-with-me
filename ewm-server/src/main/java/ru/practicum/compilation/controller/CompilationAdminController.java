package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.defaultComponent.ewmServer.dto.compilation.CompilationResponseDto;
import ru.defaultComponent.ewmServer.dto.compilation.CreateCompilationRequestDto;
import ru.defaultComponent.ewmServer.dto.compilation.UpdateCompilationRequestDto;
import ru.practicum.compilation.service.CompilationAdminService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {

    private final CompilationAdminService compilationAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto addCompilation(@Valid @RequestBody CreateCompilationRequestDto createCompilationRequestDto) {
        log.info("EWM-SERVER-admin => Запрошено добавление новой подборки => {}", createCompilationRequestDto);
        return compilationAdminService.addCompilation(createCompilationRequestDto);
    }

    @PatchMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationResponseDto updateCompilation(@Positive @PathVariable long compilationId,
                                                    @Valid @RequestBody UpdateCompilationRequestDto updateCompilationRequestDto) {
        log.info("EWM-SERVER-admin => Запрошено обновление подборки id => {}", compilationId);
        return compilationAdminService.updateCompilation(compilationId, updateCompilationRequestDto);
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@Positive @PathVariable long compilationId) {
        log.info("EWM-SERVER-admin => Запрошено удаление подборки id => {}", compilationId);
        compilationAdminService.deleteCompilation(compilationId);
    }

}
