package com.vu.api.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRespository extends JpaRepository<Product, Long> {
    boolean existsByNameIgnoreCase(String name);

    @Query("select p from Product p join fetch p.category where p.id = :id")
    Optional<Product> findByIdWithCategory(Long id);
}
