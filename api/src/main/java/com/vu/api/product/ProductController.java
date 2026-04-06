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
        ProductResponse data = service.createProduct(req);

        return ApiResponses.created(httpReq, URI.create("/products/" + data.id()), data);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id,
                                                                HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, service.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody ProductUpdateRequest req,
                                                               HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, service.updateProduct(id, req));
    }
}