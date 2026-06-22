package com.vu.api.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.vu.api.DTO.request.ProductCreationRequest;
import com.vu.api.DTO.response.ProductResponse;
import com.vu.api.ErrorConfig.ApiException;
import com.vu.api.ErrorConfig.ErrorCode;
import com.vu.api.entity.ProductEntity;
import com.vu.api.entity.ProductTypeEntity;
import com.vu.api.mapper.ProductMapper;
import com.vu.api.repository.ProductRepository;
import com.vu.api.repository.ProductTypeRepository;
import com.vu.api.service.ProductService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductTypeRepository productTypeRepository;

    @Autowired
    ProductMapper productMapper;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    @Override
    public ProductResponse createProduct(ProductCreationRequest request) {
        if (productRepository.existsByProductName(request.productName())) {
            throw new ApiException(ErrorCode.PRODUCT_NAME_IS_EXIST);
        }

        ProductTypeEntity productType = productTypeRepository
                .findByProductTypeIdAndStatusTrue(request.productTypeId())
                .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_TYPE_NOT_FOUND));

        String createUserId =
                SecurityContextHolder.getContext().getAuthentication().getName();

        ProductEntity productEntity = productMapper.toProductEntity(request);

        int newProductId = 1;
        String maxProductId = productRepository.getMaxProductId(request.productTypeId());
        if (maxProductId != null && !maxProductId.isBlank()) {
            String numberPart = maxProductId.substring(request.productTypeId().length());
            if (!numberPart.isEmpty()) {
                newProductId = Integer.parseInt(numberPart) + 1;
            }
        }
        String newProductIdStr = String.format("%s%03d", request.productTypeId(), newProductId);
        productEntity.setProductId(newProductIdStr);
        productEntity.setProductType(productType);
        productEntity.setCreateUser(createUserId);
        productEntity = productRepository.save(productEntity);
        return productMapper.toProductResponse(productEntity);
    }
}
