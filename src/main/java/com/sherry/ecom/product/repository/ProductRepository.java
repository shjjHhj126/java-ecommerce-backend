package com.sherry.ecom.product.repository;

import com.sherry.ecom.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findById(Integer id);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.category c " +
            "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:minPrice IS NULL OR COALESCE(p.discountPrice, p.price) >= :minPrice) " +
            "AND (:maxPrice IS NULL OR COALESCE(p.discountPrice, p.price) <= :maxPrice) " +
            "AND (:minDiscount IS NULL OR p.discountPrice >= :minDiscount) " +
            "AND (:discountRange IS NULL OR (p.discountPrice IS NOT NULL AND " +
            "(CAST(p.price AS float) - CAST(p.discountPrice AS float)) / CAST(p.price AS float) >= :discountRange)) " +
            "AND (:searchParam IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchParam, '%'))) " +
            "AND p.isDeleted = false " +
            "ORDER BY p")
    Page<Product> findFilteredProducts(@Param("categoryId") Integer categoryId,
                                          @Param("minPrice") Integer minPrice,
                                          @Param("maxPrice") Integer maxPrice,
                                          @Param("minDiscount") Integer minDiscount,
                                          @Param("discountRange") Float discountRange,
                                          @Param("searchParam") String searchParam,
                                          Pageable pageable);



}