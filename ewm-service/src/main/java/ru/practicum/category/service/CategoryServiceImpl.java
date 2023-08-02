package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.defaultComponent.ewmService.dto.category.CategoryRequestDto;
import ru.defaultComponent.ewmService.dto.category.CategoryResponseDto;
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
    public CategoryResponseDto addCategory(CategoryRequestDto categoryRequestDto) {
        final CategoryResponseDto categoryResponseDto = CategoryMapper
                .toCategoryResponseDto(categoryRepository.save(
                        CategoryMapper.toCategoryEntity(categoryRequestDto)));
        log.info("ADMIN => Добавлена новая категория => {}", categoryResponseDto);
        return categoryResponseDto;
    }

    @Transactional
    @Modifying
    @Override
    public CategoryResponseDto updateCategory(long categoryId, CategoryRequestDto categoryRequestDto) throws NotFoundException {
        final CategoryEntity categoryEntity = this
                .findCategoryEntityById(categoryId);
        categoryEntity.setName(
                categoryRequestDto.getName());
        final CategoryResponseDto categoryResponseDto = CategoryMapper
                .toCategoryResponseDto(
                        categoryRepository.save(categoryEntity));
        log.info("ADMIN => Обновлена категория по id => {}", categoryId);
        return categoryResponseDto;
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
    public List<CategoryResponseDto> getCategories(int from, int size) {
        final Page<CategoryResponseDto> categoryResponseDtoPage = categoryRepository
                .findAll(getPageSortAscByProperties(from, size, "id"))
                .map(CategoryMapper::toCategoryResponseDto);
        log.info("PUBLIC => Список всех категорий size => {} получен", categoryResponseDtoPage.getTotalElements());
        return categoryResponseDtoPage.getContent();
    }

    @Override
    public CategoryResponseDto getCategory(long categoryId) throws NotFoundException {
        final CategoryResponseDto categoryResponseDto = CategoryMapper
                .toCategoryResponseDto(this.findCategoryEntityById(categoryId));
        log.info("PUBLIC => Категория по id => {} получена", categoryId);
        return categoryResponseDto;
    }

}
