package ru.practicum.category.service;

import ru.defaultComponent.ewmService.dto.category.CreateCategoryRequestDto;
import ru.defaultComponent.ewmService.dto.category.CategoryResponseDto;
import ru.defaultComponent.exception.exp.ConflictException;
import ru.defaultComponent.exception.exp.NotFoundException;
import ru.practicum.category.model.CategoryEntity;

public interface CategoryAdminService {

    CategoryResponseDto addCategory(CreateCategoryRequestDto createCategoryRequestDto);

    CategoryResponseDto updateCategory(long categoryId,
                                      CreateCategoryRequestDto createCategoryRequestDto) throws NotFoundException;

    void deleteCategory(long categoryId) throws ConflictException;

    CategoryEntity findCategoryEntityById(long categoryId) throws NotFoundException;

    void checkCategoryEntityIsExistById(long categoryId) throws NotFoundException;

}
