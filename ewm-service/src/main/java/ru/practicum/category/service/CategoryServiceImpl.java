package ru.practicum.category.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.defaultComponent.ewmService.dto.category.CreateCategoryRequestDto;
import ru.defaultComponent.ewmService.dto.category.CategoryResponseDto;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.CategoryEntity;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.event.service.EventAdminService;

import java.util.List;

import static ru.defaultComponent.pageRequest.UtilPage.getPageSortAscByProperties;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryAdminService, CategoryPublicService {

    private final CategoryRepository categoryRepository;
    private final EventAdminService eventAdminService;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               @Lazy EventAdminService eventAdminService) {
        this.categoryRepository = categoryRepository;
        this.eventAdminService = eventAdminService;
    }

    @Transactional
    @Modifying
    @Override
    public CategoryResponseDto addCategory(CreateCategoryRequestDto createCategoryRequestDto) {
        final CategoryResponseDto categoryResponseDto = CategoryMapper
                .toCategoryResponseDto(categoryRepository.save(
                        CategoryMapper.toCategoryEntity(createCategoryRequestDto)));
        log.info("ADMIN => Добавлена новая категория => {}", categoryResponseDto);
        return categoryResponseDto;
    }

    @Transactional
    @Modifying
    @Override
    public CategoryResponseDto updateCategory(long categoryId, CreateCategoryRequestDto createCategoryRequestDto) throws NotFoundException {
        final CategoryEntity categoryEntity = this
                .findCategoryEntityById(categoryId);
        categoryEntity.setName(
                createCategoryRequestDto.getName());
        final CategoryResponseDto categoryResponseDto = CategoryMapper
                .toCategoryResponseDto(
                        categoryRepository.save(categoryEntity));
        log.info("ADMIN => Обновлена категория по id => {}", categoryId);
        return categoryResponseDto;
    }

    @Transactional
    @Modifying
    @Override
    public void deleteCategory(long categoryId) throws ConflictException {
        eventAdminService.checkEventsByCategoryId(categoryId);
        categoryRepository.deleteById(categoryId);
        log.info("ADMIN => Удалена категория по id => {}", categoryId);
    }

    @Override
    public CategoryEntity findCategoryEntityById(long categoryId) throws NotFoundException {
        log.info("ADMIN => Запрос категории по id => {}", categoryId);
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(
                        "ADMIN => Категория по id => " + categoryId + " не существует"));
    }

    @Override
    public void checkCategoryEntityIsExistById(long categoryId) throws NotFoundException {
        log.info("ADMIN => Запрос существует категория по id => {}", categoryId);
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("ADMIN => Категория по id => " + categoryId + " не существует");
        }
    }

    @Override
    public List<CategoryResponseDto> getAllCategories(int from, int size) {
        final Page<CategoryResponseDto> categoryResponseDtoPage = categoryRepository
                .findAll(getPageSortAscByProperties(from, size, "id"))
                .map(CategoryMapper::toCategoryResponseDto);
        log.info("PUBLIC => Список всех категорий size => {} получен", categoryResponseDtoPage.getTotalElements());
        return categoryResponseDtoPage.getContent();
    }

    @Override
    public CategoryResponseDto getCategoryById(long categoryId) throws NotFoundException {
        final CategoryResponseDto categoryResponseDto = CategoryMapper
                .toCategoryResponseDto(this.findCategoryEntityById(categoryId));
        log.info("PUBLIC => Категория по id => {} получена", categoryId);
        return categoryResponseDto;
    }

}
