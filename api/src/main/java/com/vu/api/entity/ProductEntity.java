package com.vu.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "mstproduct")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {
    @Id
    @Column(name = "product_id", unique = true, nullable = false, length = 50)
    String productId;

    @Column(name = "product_name", nullable = false, length = 200)
    String productName;

    @Column(name = "status")
    @Builder.Default
    boolean status = true;

    @Column(name = "description", length = 500)
    String description;

    @Column(name = "product_img", length = 500)
    String productImg;

    @Column(name = "product_amount", nullable = false)
    Integer productAmount;

    @Column(name = "price", nullable = false)
    Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id", nullable = false)
    ProductTypeEntity productType;

    @Column(name = "create_user", length = 8)
    String createUser;

    @Column(name = "update_user", length = 8)
    String updatedUser;

    @Column(name = "createtime", updatable = false)
    @CreationTimestamp
    LocalDateTime createdTime;

    @Column(name = "updatetime")
    @UpdateTimestamp
    LocalDateTime updatedTime;
}
