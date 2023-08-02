package ru.practicum.compilation.service;

import ru.defaultComponent.ewmService.dto.compilation.CompilationDto;
import ru.defaultComponent.ewmService.dto.compilation.CreateCompilationDto;
import ru.defaultComponent.ewmService.dto.compilation.UpdateCompilationDto;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.compilation.model.CompilationEntity;

public interface CompilationAdminService {

    CompilationDto addCompilation(CreateCompilationDto createCompilationDto);

    CompilationDto updateCompilation(long compilationId, UpdateCompilationDto updateCompilationDto) throws NotFoundException;

    void deleteCompilation(long compilationId) throws NotFoundException;

    CompilationEntity findCompilationEntityById(long compilationId) throws NotFoundException;

    void checkCompilationIsExistById(long compilationId) throws NotFoundException;

}
