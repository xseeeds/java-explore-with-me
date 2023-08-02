package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.defaultComponent.ewmService.dto.compilation.CompilationDto;
import ru.defaultComponent.ewmService.dto.compilation.UpdateCompilationDto;
import ru.defaultComponent.ewmService.dto.compilation.CreateCompilationDto;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.CompilationEntity;
import ru.practicum.compilation.dao.CompilationRepository;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.service.EventAdminService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationAdminService, CompilationPublicService {

    private final CompilationRepository compilationRepository;
    private final EventAdminService eventAdminService;

    @Transactional
    @Modifying
    @Override
    public CompilationDto addCompilation(CreateCompilationDto createCompilationDto) {
        final List<EventEntity> eventEntityList;
        if (createCompilationDto.getEvents() == null) {
            eventEntityList = List.of();
        } else {
            eventEntityList = eventAdminService.findAllByIds(createCompilationDto.getEvents());
        }
        final CompilationDto compilationDto = CompilationMapper
                .toComplicationDto(
                        compilationRepository.save(
                                CompilationMapper.toCompilationEntity(createCompilationDto, eventEntityList)));
        log.info("ADMIN => Создана новая подборка событий => {}", compilationDto);
        return compilationDto;
    }

    @Transactional
    @Modifying
    @Override
    public CompilationDto updateCompilation(long compilationId,
                                            UpdateCompilationDto compilationDto) throws NotFoundException {
        final CompilationEntity compilationEntity = this
                .findCompilationEntityById(compilationId);
        final List<EventEntity> eventEntityList;
        if (compilationDto.getEventsIds() == null) {
            eventEntityList = List.of();
        } else {
            eventEntityList = eventAdminService.findAllByIds(compilationDto.getEventsIds());
        }
        compilationEntity.setEvents(eventEntityList);
        if (compilationDto.getPinned() != null) {
            compilationEntity.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            compilationEntity.setTitle(compilationDto.getTitle());
        }
        final CompilationDto updatedCompilationDto = CompilationMapper
                .toComplicationDto(
                        compilationRepository.save(compilationEntity));
        log.info("ADMIN => Обновлена подборка событий с id => {}", compilationId);
        return updatedCompilationDto;
    }

    @Transactional
    @Modifying
    @Override
    public void deleteCompilation(long compilationId) throws NotFoundException {
        this.checkCompilationIsExistById(compilationId);
        compilationRepository.deleteById(compilationId);
        log.info("ADMIN => Удалена подборка событий с id => {}", compilationId);
    }

    @Override
    public CompilationEntity findCompilationEntityById(long compilationId) throws NotFoundException {
        log.info("ADMIN => запрос подборки по id => {} для СЕРВИСОВ", compilationId);
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException(
                        "ADMIN => Подборка по id => " + compilationId + " не существует поиск СЕРВИСОВ"));
    }

    @Override
    public void checkCompilationIsExistById(long compilationId) throws NotFoundException {
        log.info("ADMIN => Запрос существует подборка по id => {} для СЕРВИСОВ", compilationId);
        if (!compilationRepository.existsById(compilationId)) {
            throw new NotFoundException("ADMIN => Подборка по id => " + compilationId + " не существует");
        }
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        final Page<CompilationDto> compilationDtoPage = compilationRepository
                .findAllByPinned(pinned, PageRequest.of(from / size, size))
                .map(CompilationMapper::toComplicationDto);
        log.info("PUBLIC => Запрошен список подборок событий size => {} с параметром pinned => {}",
                compilationDtoPage.getTotalElements(), pinned);
        return compilationDtoPage.getContent();
    }

    @Override
    public CompilationDto getCompilationById(long compilationId) {
        final CompilationDto compilationDto = CompilationMapper
                .toComplicationDto(
                        this.findCompilationEntityById(compilationId));
        log.info("PUBLIC => Запрошена подборка событий с id => {}", compilationId);
        return compilationDto;
    }

}
