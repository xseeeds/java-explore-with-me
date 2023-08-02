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
import ru.defaultComponent.ewmService.dto.compilation.CompilationDto;
import ru.defaultComponent.ewmService.dto.compilation.CreateCompilationDto;
import ru.defaultComponent.ewmService.dto.compilation.UpdateCompilationDto;
import ru.practicum.compilation.service.CompilationAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationsControllerAdmin {

    private final CompilationAdminService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody CreateCompilationDto createCompilationDto) {
        log.info("EWM-SERVICE-admin => Запрошено добавление новой подборки => {}", createCompilationDto);
        return compilationService.addCompilation(createCompilationDto);
    }

    @PatchMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@Positive @PathVariable long compilationId,
                                            @Valid @RequestBody UpdateCompilationDto updateCompilationDto) {
        log.info("EWM-SERVICE-admin => Запрошено обновление подборки id => {}", compilationId);
        return compilationService.updateCompilation(compilationId, updateCompilationDto);
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@Positive @PathVariable long compilationId) {
        log.info("EWM-SERVICE-admin => Запрошено удаление подборки id => {}", compilationId);
        compilationService.deleteCompilation(compilationId);
    }

}
