package com.vu.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vu.api.DTO.request.ProductCreationRequest;
import com.vu.api.DTO.response.ProductResponse;

@Service
public interface ProductService {
    public List<ProductResponse> getAllProducts();

    public ProductResponse createProduct(ProductCreationRequest request);
}
