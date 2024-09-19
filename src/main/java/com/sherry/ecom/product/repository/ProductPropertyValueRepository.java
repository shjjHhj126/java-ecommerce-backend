package com.sherry.ecom.product.repository;

import com.sherry.ecom.product.model.ProductProperty;
import com.sherry.ecom.product.model.ProductPropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPropertyValueRepository extends JpaRepository<ProductPropertyValue, Integer> {
    List<ProductPropertyValue> findByProductProperty(ProductProperty property);
    Optional<ProductPropertyValue> findByProductPropertyAndValue(ProductProperty property, String value);
}
