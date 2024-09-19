package com.sherry.ecom.product.repository;

import com.sherry.ecom.product.model.Product;
import com.sherry.ecom.product.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    Optional<ProductVariant> findById(Integer id);

    List<ProductVariant> findByProduct(Product product);

    @Query("SELECT pv FROM ProductVariant pv " +
            "LEFT JOIN pv.productPropertyValueList ppv " +
            "LEFT JOIN ppv.productPropertyValueImage pvi " +
            "WHERE pv.product.id IN :productIds")
    List<ProductVariant> findVariantsAndProperties(@Param("productIds") List<Integer> productIds);

}