package ru.practicum.compilation.service;

import ru.defaultComponent.ewmService.dto.compilation.CompilationResponseDto;
import ru.defaultComponent.exception.exp.NotFoundException;

import java.util.List;

public interface CompilationPublicService {

    List<CompilationResponseDto> getCompilations(Boolean pinned, int from, int size);

    CompilationResponseDto getCompilationById(long compilationId) throws NotFoundException;

}
