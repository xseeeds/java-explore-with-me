package ru.practicum.category.service;

import ru.defaultComponent.ewmService.dto.category.CategoryResponseDto;
import ru.defaultComponent.exception.exp.NotFoundException;

import java.util.List;

public interface CategoryPublicService {

    List<CategoryResponseDto> getCategories(int from, int size);

    CategoryResponseDto getCategory(long categoryId) throws NotFoundException;

}
