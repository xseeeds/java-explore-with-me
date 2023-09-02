package ru.practicum.category.service;

import ru.defaultComponent.ewmService.dto.category.CategoryResponseDto;
import ru.defaultComponent.exception.exp.NotFoundException;
import java.util.List;

public interface CategoryPublicService {

    List<CategoryResponseDto> getAllCategories(int from, int size);

    CategoryResponseDto getCategoryById(long categoryId) throws NotFoundException;

}
