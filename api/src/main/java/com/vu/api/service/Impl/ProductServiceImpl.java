package com.vu.api.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.vu.api.DTO.request.ProductCreationRequest;
import com.vu.api.DTO.request.ProductUpdationRequest;
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

    @Override
    public Page<ProductResponse> getActiveProductsWithFilters(
            String productName, String productTypeId, String description, int page, int size) {

        // 1. Cấu hình Pageable kèm Sort để dữ liệu phân trang luôn cố định (ví dụ: sản phẩm mới lên đầu)
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());

        // 2. Chuẩn hóa dữ liệu: Biến chuỗi rỗng "" hoặc chuỗi toàn dấu cách thành null
        // Đón đầu xử lý: Chuyển thành chữ thường và bọc dấu % ngay trên Java nếu có dữ liệu
        String searchName = (productName != null && !productName.isBlank())
                ? "%" + productName.trim().toLowerCase() + "%"
                : null;

        String searchDesc = (description != null && !description.isBlank())
                ? "%" + description.trim().toLowerCase() + "%"
                : null;

        String searchTypeId = (productTypeId != null && !productTypeId.isBlank()) ? productTypeId.trim() : null;

        // 3. Truy vấn Database với các tham số đã an toàn
        Page<ProductEntity> productEntities =
                productRepository.findActiveProductsWithFilters(searchName, searchTypeId, searchDesc, pageable);

        // 4. Map sang DTO và trả về
        return productEntities.map(productMapper::toProductResponse);
    }

    @Override
    public void deleteProduct(String productId) {
        ProductEntity productEntity =
                productRepository.findById(productId).orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!productEntity.isStatus()) {
            throw new ApiException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        String deleteUserId =
                SecurityContextHolder.getContext().getAuthentication().getName();

        productEntity.setStatus(false);
        productEntity.setUpdatedUser(deleteUserId);

        productRepository.save(productEntity);
    }

    @Override
    public ProductResponse updateProduct(String productId, ProductUpdationRequest request) {
        ProductEntity productEntity =
                productRepository.findById(productId).orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!productEntity.isStatus()) {
            throw new ApiException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        String updateUserId =
                SecurityContextHolder.getContext().getAuthentication().getName();

        // Cập nhật các trường nếu có dữ liệu
        if (request.productName().isPresent()) {
            String newProductName = request.productName().get();
            if (!newProductName.equals(productEntity.getProductName())
                    && productRepository.existsByProductName(newProductName)) {
                throw new ApiException(ErrorCode.PRODUCT_NAME_IS_EXIST);
            }
            productEntity.setProductName(newProductName);
        }

        if (request.price().isPresent()) {
            productEntity.setPrice(request.price().get());
        }

        if (request.productAmount().isPresent()) {
            productEntity.setProductAmount(request.productAmount().get());
        }

        if (request.productTypeId().isPresent()) {
            String newProductTypeId = request.productTypeId().get();
            ProductTypeEntity productType = productTypeRepository
                    .findByProductTypeIdAndStatusTrue(newProductTypeId)
                    .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_TYPE_NOT_FOUND));
            productEntity.setProductType(productType);
        }

        productEntity.setUpdatedUser(updateUserId);

        productEntity = productRepository.save(productEntity);
        return productMapper.toProductResponse(productEntity);
    }
}
