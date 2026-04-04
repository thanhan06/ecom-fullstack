package com.vu.api.category;

import com.vu.api.category.DTO.CategoryCreateRequest;
import com.vu.api.category.DTO.CategoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public CategoryResponse create(CategoryCreateRequest req) {
        if (repo.existsByNameIgnoreCase(req.name())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        Category saved = repo.save(Category.builder()
                .name(req.name())
                .build());

        return new CategoryResponse(saved.getId(), saved.getName());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return repo.findAll().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();
    }
}
