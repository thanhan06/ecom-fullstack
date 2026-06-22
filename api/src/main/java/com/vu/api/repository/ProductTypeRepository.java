package com.vu.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vu.api.entity.ProductTypeEntity;

/**
 * Interface ProductTypeRepository để quản lý các thực thể ProductTypeEntity trong cơ sở dữ liệu,
 * cung cấp các phương thức CRUD để thêm, xóa và truy vấn các loại sản phẩm
 */
@Repository
public interface ProductTypeRepository extends JpaRepository<ProductTypeEntity, String> {

    public List<ProductTypeEntity> findByStatusTrue();

    public Boolean existsByProductTypeName(String productTypeName);

    @Query(value = "SELECT MAX(p.product_type_id) FROM mstproducttype p", nativeQuery = true)
    public String findMaxProductTypeId();

    // Tự động sinh câu lệnh: SELECT * FROM mstproducttype WHERE product_type_id = ? AND status = true
    public Optional<ProductTypeEntity> findByProductTypeIdAndStatusTrue(String productTypeId);
}
