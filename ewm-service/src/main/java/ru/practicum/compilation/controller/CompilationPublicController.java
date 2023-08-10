package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.defaultComponent.ewmService.dto.compilation.CompilationResponseDto;
import ru.practicum.compilation.service.CompilationPublicService;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationPublicController {

    private final CompilationPublicService compilationPublicService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationResponseDto> getComplications(@RequestParam(required = false) Boolean pinned,
                                                         @RequestParam(defaultValue = "0") int from,
                                                         @RequestParam(defaultValue = "10") int size) {
        return compilationPublicService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationResponseDto getComplicationById(@Positive @PathVariable long compilationId) {
        return compilationPublicService.getCompilationById(compilationId);
    }

}
