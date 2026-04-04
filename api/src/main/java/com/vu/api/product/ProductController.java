package com.vu.api.product;

import com.vu.api.common.ApiResponse;
import com.vu.api.common.ApiResponses;
import com.vu.api.product.DTO.ProductCreateRequest;
import com.vu.api.product.DTO.ProductResponse;
import com.vu.api.product.DTO.ProductUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductCreateRequest req,
                                                               HttpServletRequest httpReq) {
        Product saved = service.createProduct(req);

        ProductResponse data = new ProductResponse(
                saved.getId(),
                saved.getName(),
                saved.getPrice(),
                saved.getCategory().getId(),
                saved.getCategory().getName()
        );

        return ResponseEntity
                .created(URI.create("/products/" + saved.getId()))
                .body(new ApiResponse<>(Instant.now(), 201, "Created", httpReq.getRequestURI(), data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id,
                                                                HttpServletRequest httpReq) {
        Product p = service.getProductById(id);

        ProductResponse data = new ProductResponse(
                p.getId(),
                p.getName(),
                p.getPrice(),
                p.getCategory().getId(),
                p.getCategory().getName()
        );

        return ApiResponses.ok(httpReq, data);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody ProductUpdateRequest req,
                                                               HttpServletRequest httpReq) {
        Product p = service.updateProduct(id, req);

        ProductResponse data = new ProductResponse(
                p.getId(),
                p.getName(),
                p.getPrice(),
                p.getCategory().getId(),
                p.getCategory().getName()
        );

        return ApiResponses.ok(httpReq, data);
    }
}