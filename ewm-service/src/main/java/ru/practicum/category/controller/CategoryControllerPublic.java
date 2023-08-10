package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.defaultComponent.ewmService.dto.category.CategoryResponseDto;
import ru.practicum.category.service.CategoryPublicService;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryControllerPublic {

    private final CategoryPublicService categoryPublicService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryResponseDto> getAllCategories(@RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        log.info("EWM-SERVICE-public => Запрошен список всех категорий from => {}, size => {}", from, size);
        return categoryPublicService.getAllCategories(from, size);
    }

    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponseDto getCategoryById(@Positive @PathVariable long categoryId) {
        log.info("EWM-SERVICE-public => Запрошена категория id => {}", categoryId);
        return categoryPublicService.getCategoryById(categoryId);
    }

}
