package com.project.shopapp.repositories;

import com.project.shopapp.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName (String name);
    @Query("SELECT p FROM Product p " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword%) " +
            "AND (:categoryId IS NULL OR :categoryId = 0 OR p.category.id = :categoryId)")
    Page<Product> searchProduct(String keyword, Long categoryId, PageRequest pageRequest);
}
