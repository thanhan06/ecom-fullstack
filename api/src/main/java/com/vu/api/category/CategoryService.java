package com.vu.api.category;

import com.vu.api.category.DTO.CategoryCreateRequest;
import com.vu.api.category.DTO.CategoryResponse;
import com.vu.api.category.DTO.CategoryUpdateRequest;
import com.vu.api.common.ApiException;
import com.vu.api.common.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repo;
    private final CategoryMapper mapper;

    public CategoryService(CategoryRepository repo, CategoryMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional
    public CategoryResponse create(CategoryCreateRequest req) {
        if (repo.existsByNameIgnoreCase(req.name())) {
            throw new ApiException(ErrorCode.CATEGORY_NAME_EXISTS);
        }
        Category entity = mapper.toEntity(req);
        Category saved = repo.save(entity);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryUpdateRequest req) {
        Category existing = repo.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUND));

        if (!existing.getName().equalsIgnoreCase(req.name())
                && repo.existsByNameIgnoreCase(req.name())) {
            throw new ApiException(ErrorCode.CATEGORY_NAME_EXISTS);
        }

        mapper.updateEntity(existing, req);

        Category saved = repo.save(existing);
        return mapper.toResponse(saved);
    }
}