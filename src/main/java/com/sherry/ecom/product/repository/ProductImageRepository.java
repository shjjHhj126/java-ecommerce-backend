package com.sherry.ecom.product.repository;

import com.sherry.ecom.product.model.Product;
import com.sherry.ecom.product.model.image.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProduct(Product product);
}
