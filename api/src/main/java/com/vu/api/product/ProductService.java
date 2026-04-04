package com.vu.api.product;

import com.vu.api.category.Category;
import com.vu.api.category.CategoryRepository;
import com.vu.api.common.ApiException;
import com.vu.api.common.ErrorCode;
import com.vu.api.product.DTO.ProductCreateRequest;
import com.vu.api.product.DTO.ProductUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRespository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRespository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Product createProduct(ProductCreateRequest req) {
        if (productRepository.existsByNameIgnoreCase(req.name())) {
            throw new ApiException(ErrorCode.PRODUCT_NAME_EXISTS);
        }

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUND));

        Product entity = Product.builder()
                .name(req.name())
                .price(req.price())
                .category(category)
                .build();

        return productRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public Product updateProduct(Long id, ProductUpdateRequest req) {
        Product existing = getProductById(id);

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUND));

        existing.setName(req.name());
        existing.setPrice(req.price());
        existing.setCategory(category);

        return productRepository.save(existing);
    }
}