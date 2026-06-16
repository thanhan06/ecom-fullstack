package com.vu.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vu.api.DTO.request.ProductTypeRequest;
import com.vu.api.DTO.response.ProductTypeResponse;

/**
 * Lớp ProductTypeService là một lớp dịch vụ trong ứng dụng, được đánh dấu với @Service để Spring Boot có thể quản lý nó như một bean.
 * Lớp này sẽ chứa các phương thức liên quan đến quản lý loại sản phẩm (
 */
@Service
public interface ProductTypeService {
    public List<ProductTypeResponse> getAllProductType();

    public List<ProductTypeResponse> getAllProductTypeActive();

    public ProductTypeResponse createProductType(ProductTypeRequest request);

    public ProductTypeResponse updateProductType(String productTypeId, ProductTypeRequest request);
}
