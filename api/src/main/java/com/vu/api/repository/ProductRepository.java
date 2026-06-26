package com.vu.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vu.api.entity.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String> {

    boolean existsByProductName(String productName);

    @Query(
            value = "SELECT MAX(p.product_id) FROM mstproduct p WHERE p.product_id LIKE :productTypeId || '%'",
            nativeQuery = true)
    String getMaxProductId(@Param("productTypeId") String productTypeId);

    // 2. Đã thêm các từ khóa AND, khoảng trắng và sửa lỗi chính tả description
    @EntityGraph(attributePaths = {"productType"})
    @Query(
            value = "SELECT p FROM ProductEntity p WHERE "
                    + "(:productName IS NULL OR LOWER(p.productName) LIKE :productName) AND "
                    + "(:productTypeId IS NULL OR p.productType.productTypeId = :productTypeId) AND "
                    + "(:description IS NULL OR LOWER(p.description) LIKE :description) AND "
                    + "p.status = true AND "
                    + "p.productType.status = true")
    Page<ProductEntity> findActiveProductsWithFilters(
            @Param("productName") String productName,
            @Param("productTypeId") String productTypeId,
            @Param("description") String description,
            Pageable pageable);
}
