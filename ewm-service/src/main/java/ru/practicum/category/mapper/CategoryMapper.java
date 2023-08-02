package ru.practicum.category.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.ewmService.dto.category.CategoryDto;
import ru.practicum.category.model.CategoryEntity;

@UtilityClass
public class CategoryMapper {

    public CategoryDto toCategoryDto(CategoryEntity categoryEntity) {
        return CategoryDto
                .builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .build();
    }

    public CategoryEntity toCategoryEntity(CategoryDto categoryDto) {
        return CategoryEntity
                .builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

}
