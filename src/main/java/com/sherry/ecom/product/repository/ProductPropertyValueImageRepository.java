package com.sherry.ecom.product.repository;

import com.sherry.ecom.product.model.ProductPropertyValue;
import com.sherry.ecom.product.model.image.ProductPropertyValueImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ProductPropertyValueImageRepository extends JpaRepository<ProductPropertyValueImage, Integer> {
    List<ProductPropertyValueImage> findByProductPropertyValue(ProductPropertyValue productPropertyValue);

}
