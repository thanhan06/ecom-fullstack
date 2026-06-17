package com.vu.api.DTO.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Record ProductTypeRequest để nhận dữ liệu từ phía client khi tạo hoặc cập nhật loại sản phẩm,
 * bao gồm productTypeName (tên loại sản phẩm) và các ràng buộc hợp lệ để đảm bảo dữ liệu đầu vào.
 */
public record ProductTypeRequest(@NotBlank(message = "PRODUCT_TYPE_NAME_IS_NOT_BLANK") String productTypeName) {}
