package com.project.shopapp.repositories;

import com.project.shopapp.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName (String name);
//    Page<Product> findAll(Pageable pageable);
}
