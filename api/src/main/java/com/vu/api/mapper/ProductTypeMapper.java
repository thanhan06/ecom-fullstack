package com.vu.api.mapper;

import org.mapstruct.Mapper;

import com.vu.api.DTO.response.ProductTypeResponse;
import com.vu.api.entity.ProductTypeEntity;

@Mapper(componentModel = "spring")
public interface ProductTypeMapper {
    public ProductTypeResponse toProductTypeResponse(ProductTypeEntity entity);
}
