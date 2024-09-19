package com.sherry.ecom.product.service;

import com.sherry.ecom.product.Response.PropertyResponse;
import com.sherry.ecom.product.model.Product;
import com.sherry.ecom.product.model.ProductProperty;
import com.sherry.ecom.product.model.ProductPropertyValue;
import com.sherry.ecom.product.repository.ProductPropertyRepository;
import com.sherry.ecom.product.repository.ProductPropertyValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductPropertyService {

    private final ProductPropertyRepository productPropertyRepository;
    private final ProductPropertyValueRepository productPropertyValueRepository;

    public List<ProductProperty> getPropertiesByProduct(Product product) {
        return productPropertyRepository.findByProduct(product);
    }

    public List<ProductPropertyValue> getValuesByProperty(ProductProperty property) {
        return productPropertyValueRepository.findByProductProperty(property);
    }

    public ProductProperty create(ProductProperty productProperty) {
        return productPropertyRepository.save(productProperty);
    }

    public ProductProperty update(ProductProperty productProperty) {
        return productPropertyRepository.save(productProperty);
    }

    public Optional<ProductProperty> getPropertyByNameAndProduct(String name, Product product) {
        return productPropertyRepository.findByNameAndProduct(name, product);
    }

}

