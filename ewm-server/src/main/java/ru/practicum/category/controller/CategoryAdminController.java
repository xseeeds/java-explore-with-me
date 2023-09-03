package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.defaultComponent.ewmServer.dto.category.CreateCategoryRequestDto;
import ru.defaultComponent.ewmServer.dto.category.CategoryResponseDto;
import ru.practicum.category.service.CategoryAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {

    private final CategoryAdminService categoryAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto addCategory(@Valid @RequestBody CreateCategoryRequestDto createCategoryRequestDto) {
        log.info("EWM-SERVER-admin => Запрошено добавление новой категории => {}", createCategoryRequestDto);
        return categoryAdminService.addCategory(createCategoryRequestDto);
    }

    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponseDto updateCategory(@Positive @PathVariable long categoryId,
                                             @Valid @RequestBody CreateCategoryRequestDto createCategoryRequestDto) {
        log.info("EWM-SERVER-admin => Запрошено обновление категории по id => {} => {}", categoryId, createCategoryRequestDto);
        return categoryAdminService.updateCategory(categoryId, createCategoryRequestDto);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@Positive @PathVariable long categoryId) {
        log.info("EWM-SERVER-admin => Запрошено удаление категории по id => {}", categoryId);
        categoryAdminService.deleteCategory(categoryId);
    }

}
