package com.vu.api.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRespository extends JpaRepository<Product, Long> {
    boolean existsByNameIgnoreCase(String name);

    @Query("select p from Product p join fetch p.category where p.id = :id")
    Optional<Product> findByIdWithCategory(Long id);
}