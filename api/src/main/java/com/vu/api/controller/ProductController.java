package com.vu.api.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vu.api.DTO.request.ProductCreationRequest;
import com.vu.api.DTO.request.ProductUpdationRequest;
import com.vu.api.DTO.response.ProductResponse;
import com.vu.api.ResponseConfig.ApiResponse;
import com.vu.api.ResponseConfig.ApiResponses;
import com.vu.api.service.ProductService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(HttpServletRequest request) {
        List<ProductResponse> products = productService.getAllProducts();
        return ApiResponses.ok(request, products);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestBody @Valid ProductCreationRequest request, HttpServletRequest req) {
        ProductResponse product = productService.createProduct(request);
        return ApiResponses.ok(req, product);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getActiveProductsWithFilters(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String productTypeId,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            HttpServletRequest request,
            HttpServletRequest req) {
        Page<ProductResponse> products =
                productService.getActiveProductsWithFilters(productName, productTypeId, description, page, size);
        return ApiResponses.ok(req, products);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String productId, HttpServletRequest req) {
        productService.deleteProduct(productId);
        return ApiResponses.ok(req, null);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/update/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable String productId,
            @RequestBody @Valid ProductUpdationRequest request,
            HttpServletRequest req) {
        ProductResponse updatedProduct = productService.updateProduct(productId, request);
        return ApiResponses.ok(req, updatedProduct);
    }
}
