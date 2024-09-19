package com.sherry.ecom.product.service;

import com.sherry.ecom.product.model.ProductPropertyValue;
import com.sherry.ecom.product.model.ProductVariant;
import com.sherry.ecom.product.repository.ProductPropertyValueRepository;
import com.sherry.ecom.product.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

    private final ProductVariantRepository repository;

    public ProductVariant create(ProductVariant productVariant) {
        return repository.save(productVariant);
    }

    public ProductVariant update(ProductVariant variant) {
        return repository.save(variant);
    }

    public ProductVariant addPropertyValuesToVariant(ProductVariant variant, List<ProductPropertyValue> propertyValues){
        variant.setProductPropertyValueList(propertyValues);
        return variant;
    }

    public Optional<ProductVariant> findById(Integer id) {
        return repository.findById(id);
    }
}
