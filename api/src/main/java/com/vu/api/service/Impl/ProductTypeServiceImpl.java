package com.vu.api.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.vu.api.DTO.request.ProductTypeRequest;
import com.vu.api.DTO.response.ProductTypeResponse;
import com.vu.api.ErrorConfig.ApiException;
import com.vu.api.ErrorConfig.ErrorCode;
import com.vu.api.entity.ProductTypeEntity;
import com.vu.api.mapper.ProductTypeMapper;
import com.vu.api.repository.ProductTypeRepository;
import com.vu.api.service.ProductTypeService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductTypeServiceImpl implements ProductTypeService {
    @Autowired
    ProductTypeRepository productTypeRepository;

    @Autowired
    ProductTypeMapper productTypeMapper;

    @Override
    public List<ProductTypeResponse> getAllProductType() {
        List<ProductTypeEntity> productTypeEntities = productTypeRepository.findAll();
        return productTypeEntities.stream()
                .map(entity -> productTypeMapper.toProductTypeResponse(entity))
                .toList();
    }

    @Override
    public List<ProductTypeResponse> getAllProductTypeActive() {
        List<ProductTypeEntity> productTypeEntities = productTypeRepository.findByStatusTrue();
        return productTypeEntities.stream()
                .map(entity -> productTypeMapper.toProductTypeResponse(entity))
                .toList();
    }

    @Override
    public ProductTypeResponse createProductType(ProductTypeRequest request) {
        if (productTypeRepository.existsByProductTypeName(request.productTypeName())) {
            throw new ApiException(ErrorCode.PRODUCT_TYPE_NAME_IS_EXIST);
        }

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        String maxProductTypeId = productTypeRepository.findMaxProductTypeId();

        int newProductTypeId = 1; // Mặc định nếu chưa có loại sản phẩm nào
        if (maxProductTypeId != null && !maxProductTypeId.isEmpty()) {
            newProductTypeId = Integer.parseInt(maxProductTypeId) + 1;
        }
        String newProductTypeIdStr = String.format("%03d", newProductTypeId);

        ProductTypeEntity productTypeEntity = new ProductTypeEntity();

        productTypeEntity.setProductTypeId(newProductTypeIdStr);
        productTypeEntity.setProductTypeName(request.productTypeName());
        productTypeEntity.setCreateUser(userId);

        ProductTypeEntity savedEntity = productTypeRepository.save(productTypeEntity);
        return productTypeMapper.toProductTypeResponse(savedEntity);
    }

    @Override
    public ProductTypeResponse updateProductType(String productTypeId, ProductTypeRequest request) {
        ProductTypeEntity productTypeEntity = productTypeRepository
                .findById(productTypeId)
                .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_TYPE_NOT_FOUND));

        if (productTypeRepository.existsByProductTypeName(request.productTypeName())
                && !productTypeEntity.getProductTypeName().equals(request.productTypeName())) {
            throw new ApiException(ErrorCode.PRODUCT_TYPE_NAME_IS_EXIST);
        }

        productTypeEntity.setProductTypeName(request.productTypeName());
        productTypeEntity.setUpdatedUser(
                SecurityContextHolder.getContext().getAuthentication().getName());

        ProductTypeEntity updatedEntity = productTypeRepository.save(productTypeEntity);
        return productTypeMapper.toProductTypeResponse(updatedEntity);
    }
}
