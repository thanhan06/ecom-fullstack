package com.vu.api.category;

import com.vu.api.category.DTO.CategoryCreateRequest;
import com.vu.api.category.DTO.CategoryResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public CategoryResponse create(@Valid @RequestBody CategoryCreateRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<CategoryResponse> list() {
        return service.list();
    }
}