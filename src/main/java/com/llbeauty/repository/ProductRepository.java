package com.llbeauty.repository;

import com.llbeauty.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);

    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR :category = '' OR p.category = :category) AND " +
           "(:search IS NULL OR :search = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR p.status = :status)")
    Page<Product> searchProductsPaged(@Param("category") String category, 
                                     @Param("search") String search, 
                                     @Param("status") String status, 
                                     Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR :category = '' OR p.category = :category) AND " +
           "(:search IS NULL OR :search = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR p.status = :status)")
    List<Product> searchProductsList(@Param("category") String category, 
                                     @Param("search") String search, 
                                     @Param("status") String status);
}
