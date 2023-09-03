package ru.practicum.compilation.service;

import ru.defaultComponent.ewmServer.dto.compilation.CompilationResponseDto;
import ru.defaultComponent.ewmServer.dto.compilation.CreateCompilationRequestDto;
import ru.defaultComponent.ewmServer.dto.compilation.UpdateCompilationRequestDto;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.compilation.model.CompilationEntity;

public interface CompilationAdminService {

    CompilationResponseDto addCompilation(CreateCompilationRequestDto createCompilationRequestDto);

    CompilationResponseDto updateCompilation(long compilationId, UpdateCompilationRequestDto updateCompilationRequestDto) throws NotFoundException;

    void deleteCompilation(long compilationId) throws NotFoundException;

    CompilationEntity findCompilationEntityById(long compilationId) throws NotFoundException;

    void checkCompilationEntityIsExistById(long compilationId) throws NotFoundException;

}
