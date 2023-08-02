package ru.practicum.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.ewmService.dto.compilation.CompilationDto;
import ru.defaultComponent.ewmService.dto.compilation.CreateCompilationDto;
import ru.practicum.compilation.model.CompilationEntity;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.EventEntity;

import java.util.List;

import static java.util.stream.Collectors.toList;


@UtilityClass
public class CompilationMapper {

    public CompilationDto toComplicationDto(CompilationEntity compilationEntity) {
        return CompilationDto
                .builder()
                .id(compilationEntity.getId())
                .title(compilationEntity.getTitle())
                .pinned(compilationEntity.getPinned())
                .events(compilationEntity
                        .getEvents()
                        .stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(toList()))
                .build();
    }

    public CompilationEntity toCompilationEntity(CreateCompilationDto createCompilationDto,
                                                 List<EventEntity> eventEntityList) {
        return CompilationEntity
                .builder()
                .events(eventEntityList)
                .pinned(createCompilationDto.getPinned())
                .title(createCompilationDto.getTitle())
                .build();
    }

}
