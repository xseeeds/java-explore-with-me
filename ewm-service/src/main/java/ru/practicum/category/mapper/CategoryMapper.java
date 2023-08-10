package ru.practicum.category.mapper;

import lombok.experimental.UtilityClass;
import ru.defaultComponent.ewmService.dto.category.CreateCategoryRequestDto;
import ru.defaultComponent.ewmService.dto.category.CategoryResponseDto;
import ru.practicum.category.model.CategoryEntity;

@UtilityClass
public class CategoryMapper {

    public CategoryResponseDto toCategoryResponseDto(CategoryEntity categoryEntity) {
        return CategoryResponseDto
                .builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .build();
    }

    public CategoryEntity toCategoryEntity(CreateCategoryRequestDto createCategoryRequestDto) {
        return CategoryEntity
                .builder()
                .id(createCategoryRequestDto.getId())
                .name(createCategoryRequestDto.getName())
                .build();
    }

}
