package ru.practicum.category.service;

import ru.defaultComponent.ewmService.dto.category.CategoryRequestDto;
import ru.defaultComponent.ewmService.dto.category.CategoryResponseDto;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.category.model.CategoryEntity;

public interface CategoryAdminService {

    CategoryResponseDto addCategory(CategoryRequestDto categoryRequestDto);

    CategoryResponseDto updateCategory(long categoryId,
                                      CategoryRequestDto categoryRequestDto) throws NotFoundException;

    void deleteCategory(long categoryId) throws NotFoundException;

    CategoryEntity findCategoryEntityById(long categoryId) throws NotFoundException;

    void checkCategoryIsExistById(long categoryId) throws NotFoundException;

}
