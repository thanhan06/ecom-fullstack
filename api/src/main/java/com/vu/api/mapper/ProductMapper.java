package com.vu.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vu.api.DTO.request.ProductCreationRequest;
import com.vu.api.DTO.response.ProductResponse;
import com.vu.api.entity.ProductEntity;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "productType.productTypeId", target = "productTypeId")
    public ProductResponse toProductResponse(ProductEntity entity);

    @Mapping(
            target = "productType",
            ignore = true) // Bỏ qua trường productType khi mapping từ ProductCreationRequest sang ProductEntity
    public ProductEntity toProductEntity(ProductCreationRequest request);
}
