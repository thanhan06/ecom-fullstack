package com.vu.api.DTO.response;

import java.time.LocalDateTime;

/**
 * Class ProductTypeResponse để trả về dữ liệu liên quan đến loại sản phẩm, bao gồm:
 * - productTypeId: Mã định danh của loại sản phẩm.
 * - productTypeName: Tên của loại sản phẩm.
 * - status: Trạng thái của loại sản phẩm (true nếu đang hoạt động, false
 * nếu không hoạt động).
 * - createdAt: Thời gian tạo loại sản phẩm.
 * - createUser: Người tạo loại sản phẩm.
 * - updatedAt: Thời gian cập nhật loại sản phẩm.
 * - updatedUser: Người cập nhật loại sản phẩm.
 * Class này được sử dụng để trả về thông tin về loại sản phẩm cho client sau khi
 */
public record ProductTypeResponse(
        String productTypeId,
        String productTypeName,
        boolean status,
        LocalDateTime createdTime,
        String createUser,
        LocalDateTime updatedTime,
        String updatedUser) {}
