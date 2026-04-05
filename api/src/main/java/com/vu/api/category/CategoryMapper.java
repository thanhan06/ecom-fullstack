package com.vu.api.category;

import com.vu.api.category.DTO.CategoryCreateRequest;
import com.vu.api.category.DTO.CategoryResponse;
import com.vu.api.category.DTO.CategoryUpdateRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryCreateRequest request);

    CategoryResponse toResponse(Category entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Category entity, CategoryUpdateRequest req);
}