package ru.practicum.category.service;

import ru.defaultComponent.ewmService.dto.category.CategoryDto;
import ru.defaultComponent.exception.exp.NotFoundException;

import java.util.List;

public interface CategoryPublicService {

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long categoryId) throws NotFoundException;

}
