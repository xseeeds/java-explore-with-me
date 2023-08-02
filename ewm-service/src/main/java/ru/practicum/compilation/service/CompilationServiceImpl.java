package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.defaultComponent.ewmService.dto.compilation.CompilationResponseDto;
import ru.defaultComponent.ewmService.dto.compilation.UpdateCompilationRequestDto;
import ru.defaultComponent.ewmService.dto.compilation.CreateCompilationRequestDto;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.CompilationEntity;
import ru.practicum.compilation.dao.CompilationRepository;
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
    public CompilationResponseDto addCompilation(CreateCompilationRequestDto createCompilationRequestDto) {
        final CompilationEntity compilationEntity = CompilationMapper.toCompilationEntity(createCompilationRequestDto);
        if (createCompilationRequestDto.getEvents() != null) {
            compilationEntity.setEvents(
                    eventAdminService.findAllByIds(
                            createCompilationRequestDto.getEvents()));
        }
        final CompilationResponseDto compilationResponseDto = CompilationMapper
                .toCompilationResponseDto(
                        compilationRepository.save(compilationEntity));
        log.info("ADMIN => Создана новая подборка событий => {}", compilationResponseDto);
        return compilationResponseDto;
    }

    @Transactional
    @Modifying
    @Override
    public CompilationResponseDto updateCompilation(long compilationId,
                                                    UpdateCompilationRequestDto updateCompilationRequestDto) throws NotFoundException {
        final CompilationEntity compilationEntity = this.findCompilationEntityById(compilationId);
        if (updateCompilationRequestDto.getEvents() != null) {
            compilationEntity.setEvents(
                    eventAdminService.findAllByIds(
                                    updateCompilationRequestDto.getEvents()));
        }
        if (updateCompilationRequestDto.getPinned() != null) {
            compilationEntity.setPinned(updateCompilationRequestDto.getPinned());
        }
        if (updateCompilationRequestDto.getTitle() != null) {
            compilationEntity.setTitle(updateCompilationRequestDto.getTitle());
        }
        final CompilationResponseDto updatedCompilationResponseDto = CompilationMapper
                .toCompilationResponseDto(
                        compilationRepository.save(compilationEntity));
        log.info("ADMIN => Обновлена подборка событий с id => {}", compilationId);
        return updatedCompilationResponseDto;
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
    public List<CompilationResponseDto> getCompilations(Boolean pinned, int from, int size) {
        final Page<CompilationResponseDto> compilationDtoPage = compilationRepository
                .findAllByPinned(pinned, PageRequest.of(from / size, size))
                .map(CompilationMapper::toCompilationResponseDto);
        log.info("PUBLIC => Запрошен список подборок событий size => {} с параметром pinned => {}",
                compilationDtoPage.getTotalElements(), pinned);
        return compilationDtoPage.getContent();
    }

    @Override
    public CompilationResponseDto getCompilationById(long compilationId) {
        final CompilationResponseDto compilationResponseDto = CompilationMapper
                .toCompilationResponseDto(
                        this.findCompilationEntityById(compilationId));
        log.info("PUBLIC => Запрошена подборка событий с id => {}", compilationId);
        return compilationResponseDto;
    }

}
