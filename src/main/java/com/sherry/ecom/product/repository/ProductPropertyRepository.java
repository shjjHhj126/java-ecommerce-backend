package com.sherry.ecom.product.repository;

import com.sherry.ecom.product.model.Product;
import com.sherry.ecom.product.model.ProductProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface ProductPropertyRepository extends JpaRepository<ProductProperty, Integer> {
    List<ProductProperty> findByProduct(Product product);
    Optional<ProductProperty> findByNameAndProduct(String name, Product product);

}
