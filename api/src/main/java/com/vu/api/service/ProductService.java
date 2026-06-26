package com.vu.api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.vu.api.DTO.request.ProductCreationRequest;
import com.vu.api.DTO.response.ProductResponse;

@Service
public interface ProductService {
    public List<ProductResponse> getAllProducts();

    public ProductResponse createProduct(ProductCreationRequest request);

    public Page<ProductResponse> getActiveProductsWithFilters(
            String productName, String productTypeId, String description, int page, int size);
}
