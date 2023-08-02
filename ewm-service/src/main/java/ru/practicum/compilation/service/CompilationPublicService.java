package ru.practicum.compilation.service;

import ru.defaultComponent.ewmService.dto.compilation.CompilationDto;
import ru.defaultComponent.exception.exp.NotFoundException;

import java.util.List;

public interface CompilationPublicService {

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(long compilationId) throws NotFoundException;

}
