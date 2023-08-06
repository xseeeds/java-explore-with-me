package ru.practicum.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.ewmService.dto.compilation.CompilationResponseDto;
import ru.defaultComponent.ewmService.dto.compilation.CreateCompilationRequestDto;
import ru.practicum.compilation.model.CompilationEntity;
import ru.practicum.event.mapper.EventMapper;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;


@UtilityClass
public class CompilationMapper {

    public CompilationResponseDto toCompilationResponseDto(CompilationEntity compilationEntity) {
        return CompilationResponseDto
                .builder()
                .id(compilationEntity.getId())
                .title(compilationEntity.getTitle())
                .pinned(compilationEntity.getPinned())
                .events(compilationEntity
                        .getEvents()
                        .stream()
                        .map(EventMapper::toEventShortResponseDto)
                        .collect(toList()))
                .build();
    }

    public CompilationEntity toCompilationEntity(CreateCompilationRequestDto createCompilationRequestDto) {
        return CompilationEntity
                .builder()
                .events(emptyList())
                .pinned(createCompilationRequestDto.getPinned())
                .title(createCompilationRequestDto.getTitle())
                .build();
    }

}
