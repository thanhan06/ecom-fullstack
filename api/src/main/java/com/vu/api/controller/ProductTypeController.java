package com.vu.api.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vu.api.DTO.request.ProductTypeRequest;
import com.vu.api.DTO.response.ProductTypeResponse;
import com.vu.api.ResponseConfig.ApiResponse;
import com.vu.api.ResponseConfig.ApiResponses;
import com.vu.api.service.ProductTypeService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/product-types")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductTypeController {
    @Autowired
    ProductTypeService productTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductTypeResponse>>> getAllProductType(HttpServletRequest req) {
        List<ProductTypeResponse> productTypes = productTypeService.getAllProductType();
        return ApiResponses.ok(req, productTypes);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ProductTypeResponse>>> getAllProductTypeActive(HttpServletRequest req) {
        List<ProductTypeResponse> productTypes = productTypeService.getAllProductTypeActive();
        return ApiResponses.ok(req, productTypes);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProductTypeResponse>> createProductType(
            HttpServletRequest req, @RequestBody @Valid ProductTypeRequest request) {
        return ApiResponses.created(req, productTypeService.createProductType(request));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/update/{productTypeId}")
    public ResponseEntity<ApiResponse<ProductTypeResponse>> updateProductType(
            HttpServletRequest req,
            @PathVariable String productTypeId,
            @RequestBody @Valid ProductTypeRequest request) {
        return ApiResponses.ok(req, productTypeService.updateProductType(productTypeId, request));
    }
}
