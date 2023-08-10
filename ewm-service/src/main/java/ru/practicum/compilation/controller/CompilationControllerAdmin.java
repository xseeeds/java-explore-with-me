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
import ru.defaultComponent.ewmService.dto.compilation.CompilationResponseDto;
import ru.defaultComponent.ewmService.dto.compilation.CreateCompilationRequestDto;
import ru.defaultComponent.ewmService.dto.compilation.UpdateCompilationRequestDto;
import ru.practicum.compilation.service.CompilationAdminService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationControllerAdmin {

    private final CompilationAdminService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto addCompilation(@Valid @RequestBody CreateCompilationRequestDto createCompilationRequestDto) {
        log.info("EWM-SERVICE-admin => Запрошено добавление новой подборки => {}", createCompilationRequestDto);
        return compilationService.addCompilation(createCompilationRequestDto);
    }

    @PatchMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationResponseDto updateCompilation(@Positive @PathVariable long compilationId,
                                                    @Valid @RequestBody UpdateCompilationRequestDto updateCompilationRequestDto) {
        log.info("EWM-SERVICE-admin => Запрошено обновление подборки id => {}", compilationId);
        return compilationService.updateCompilation(compilationId, updateCompilationRequestDto);
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@Positive @PathVariable long compilationId) {
        log.info("EWM-SERVICE-admin => Запрошено удаление подборки id => {}", compilationId);
        compilationService.deleteCompilation(compilationId);
    }

}
