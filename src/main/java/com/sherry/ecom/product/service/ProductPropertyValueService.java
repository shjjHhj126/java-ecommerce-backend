package com.sherry.ecom.product.service;

import com.sherry.ecom.product.model.Product;
import com.sherry.ecom.product.model.ProductProperty;
import com.sherry.ecom.product.model.ProductPropertyValue;
import com.sherry.ecom.product.repository.ProductPropertyValueRepository;
import com.sherry.ecom.product.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductPropertyValueService {
    private final ProductPropertyValueRepository repository;

    public Optional<ProductPropertyValue> getPropertyValueByPropertyAndValue(ProductProperty pp, String val) {
        return repository.findByProductPropertyAndValue(pp, val);
    }

    public ProductPropertyValue create(ProductPropertyValue value) {
        return repository.save(value);
    }
}
