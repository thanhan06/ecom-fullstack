package com.vu.api.category;

import com.vu.api.category.DTO.CategoryCreateRequest;
import com.vu.api.category.DTO.CategoryResponse;
import com.vu.api.category.DTO.CategoryUpdateRequest;
import com.vu.api.common.ApiResponse;
import com.vu.api.common.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryCreateRequest req,
                                                                HttpServletRequest httpReq) {
        CategoryResponse data = service.create(req);

        // nếu muốn Location header:
        return ResponseEntity
                .created(URI.create("/categories/" + data.id()))
                .body(ApiResponses.created(httpReq, data).getBody()); // tạm thời

        // nếu không cần Location:
        // return ApiResponses.created(httpReq, data);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> list(HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, service.list());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody CategoryUpdateRequest req,
                                                                HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, service.update(id, req));
    }
}