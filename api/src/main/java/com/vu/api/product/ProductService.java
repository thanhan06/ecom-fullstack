package com.vu.api.product;

import com.vu.api.category.Category;
import com.vu.api.category.CategoryRepository;
import com.vu.api.common.ApiException;
import com.vu.api.common.ErrorCode;
import com.vu.api.product.DTO.ProductCreateRequest;
import com.vu.api.product.DTO.ProductResponse;
import com.vu.api.product.DTO.ProductUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRespository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRespository productRepository,
                          CategoryRepository categoryRepository,
                          ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest req) {
        if (productRepository.existsByNameIgnoreCase(req.name())) {
            throw new ApiException(ErrorCode.PRODUCT_NAME_EXISTS);
        }

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUND));

        Product entity = productMapper.toEntity(req);
        entity.setCategory(category);

        Product saved = productRepository.save(entity);
        return productMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product p = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_NOT_FOUND));

        return productMapper.toResponse(p);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest req) {
        Product existing = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_NOT_FOUND));

        // (khuyến nghị) check trùng tên khi update
        if (!existing.getName().equalsIgnoreCase(req.name())
                && productRepository.existsByNameIgnoreCase(req.name())) {
            throw new ApiException(ErrorCode.PRODUCT_NAME_EXISTS);
        }

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUND));

        productMapper.updateEntity(existing, req);
        existing.setCategory(category);

        Product saved = productRepository.save(existing);
        return productMapper.toResponse(saved);
    }
}