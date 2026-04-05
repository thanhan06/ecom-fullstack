package com.vu.api.product;

import com.vu.api.category.Category;
import com.vu.api.product.DTO.ProductCreateRequest;
import com.vu.api.product.DTO.ProductResponse;
import com.vu.api.product.DTO.ProductUpdateRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Entity -> Response (lấy từ relation category)
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductResponse toResponse(Product entity);

    // CreateRequest -> Entity (category sẽ set từ service, không map từ categoryId ở đây)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductCreateRequest req);

    // Update: cập nhật field vào entity có sẵn
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntity(@MappingTarget Product entity, ProductUpdateRequest req);

    // Helper để set category (service đã load)
    @AfterMapping
    default void setCategory(@MappingTarget Product product, @Context Category category) {
        if (category != null) product.setCategory(category);
    }

    // Overload: tạo entity + set category
    @Mapping(target = "category", ignore = true)
    default Product toEntity(ProductCreateRequest req, @Context Category category) {
        Product p = toEntity(req);
        setCategory(p, category);
        return p;
    }
}