package ru.practicum.category.service;

import ru.defaultComponent.ewmService.dto.category.CategoryDto;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.category.model.CategoryEntity;

public interface CategoryAdminService {

    CategoryDto addCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(long categoryId,
                               CategoryDto categoryDto) throws NotFoundException;

    void deleteCategory(long categoryId) throws NotFoundException;

    CategoryEntity findCategoryEntityById(long categoryId) throws NotFoundException;

    void checkCategoryIsExistById(long categoryId) throws NotFoundException;

}
