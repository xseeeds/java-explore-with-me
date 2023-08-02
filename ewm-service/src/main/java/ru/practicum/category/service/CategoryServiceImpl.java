package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.defaultComponent.ewmService.dto.category.CategoryDto;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.CategoryEntity;
import ru.practicum.category.dao.CategoryRepository;

import java.util.List;

import static ru.defaultComponent.pageRequest.UtilPage.getPageSortAscByProperties;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryAdminService, CategoryPublicService {

    private final CategoryRepository categoryRepository;

    @Transactional
    @Modifying
    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        final CategoryDto createdCategoryDto = CategoryMapper
                .toCategoryDto(categoryRepository.save(
                        CategoryMapper.toCategoryEntity(categoryDto)));
        log.info("ADMIN => Добавлена новая категория => {}", createdCategoryDto);
        return createdCategoryDto;
    }

    @Transactional
    @Modifying
    @Override
    public CategoryDto updateCategory(long categoryId, CategoryDto categoryDto) throws NotFoundException {
        final CategoryEntity categoryEntity = this
                .findCategoryEntityById(categoryId);
        categoryEntity.setName(
                categoryDto.getName());
        final CategoryDto updatedCategoryDto = CategoryMapper
                .toCategoryDto(
                        categoryRepository.save(categoryEntity));
        log.info("ADMIN => Обновлена категория по id => {}", categoryId);
        return updatedCategoryDto;
    }

    @Transactional
    @Modifying
    @Override
    public void deleteCategory(long categoryId) throws NotFoundException {
        this.checkCategoryIsExistById(categoryId);
        categoryRepository.deleteById(categoryId);
        log.info("ADMIN => Удалена категория по id => {}", categoryId);
    }

    @Override
    public CategoryEntity findCategoryEntityById(long categoryId) throws NotFoundException {
        log.info("ADMIN => Запрос вещи по id => {} для СЕРВИСОВ", categoryId);
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(
                        "ADMIN => Категория по id => " + categoryId + " не существует поиск СЕРВИСОВ"));
    }

    @Override
    public void checkCategoryIsExistById(long categoryId) throws NotFoundException {
        log.info("ADMIN => Запрос существует категория по id => {} для СЕРВИСОВ", categoryId);
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("ADMIN => Категория по id => " + categoryId + " не существует");
        }
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        final Page<CategoryDto> categoryDtoPage = categoryRepository
                .findAll(getPageSortAscByProperties(from, size, "id"))
                .map(CategoryMapper::toCategoryDto);
        log.info("PUBLIC => Список всех категорий size => {} получен", categoryDtoPage.getTotalElements());
        return categoryDtoPage.getContent();
    }

    @Override
    public CategoryDto getCategory(long categoryId) throws NotFoundException {
        final CategoryDto categoryDto = CategoryMapper
                .toCategoryDto(this.findCategoryEntityById(categoryId));
        log.info("PUBLIC => Категория по id => {} получена", categoryId);
        return categoryDto;
    }

}
