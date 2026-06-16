package com.vu.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Class ProductTypeEntity để lưu trữ thông tin về loại sản phẩm trong cơ sở dữ liệu,
 * bao gồm productTypeId (ID loại sản phẩm), productTypeName (tên loại sản phẩm), status (trạng thái hoạt động),
 * created_at (thời gian tạo), updated_at (thời gian cập nhật), create
 * _psn_cd (ID người tạo) và updated_psn_id (ID người cập nhật).
 */
@Entity
@Table(name = "mstproducttype")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ProductTypeEntity {
    @Id
    @Column(name = "product_type_id", unique = true, nullable = false, length = 50)
    String productTypeId;

    @Column(name = "name", nullable = false, length = 200)
    String productTypeName;

    @Column(name = "status")
    @Builder.Default
    boolean status = true;

    @CreationTimestamp
    @Column(name = "createtime", updatable = false)
    LocalDateTime createdTime;

    @UpdateTimestamp
    @Column(name = "updatetime")
    LocalDateTime updatedTime;

    @Column(name = "create_user", length = 50)
    @Builder.Default
    String createUser = "system";

    @Column(name = "update_user", length = 50)
    @Builder.Default
    String updatedUser = "system";
}
