package com.vu.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vu.api.entity.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String> {

    boolean existsByProductName(String productName);

    // Dùng || '%' để lấy tất cả product_id BẮT ĐẦU BẰNG productTypeId
    @Query(
            value = "SELECT MAX(p.product_id) FROM mstproduct p WHERE p.product_id LIKE :productTypeId || '%'",
            nativeQuery = true)
    String getMaxProductId(@Param("productTypeId") String productTypeId);
}
