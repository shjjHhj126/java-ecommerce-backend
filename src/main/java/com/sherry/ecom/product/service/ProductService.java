package com.sherry.ecom.product.service;

import com.sherry.ecom.category.Category;
import com.sherry.ecom.exception.ResourceNotFoundException;
import com.sherry.ecom.product.Response.ProductResponse;
import com.sherry.ecom.product.Response.ProductVariantResponse;
import com.sherry.ecom.product.Response.PropertyResponse;
import com.sherry.ecom.product.model.Product;
import com.sherry.ecom.product.model.ProductProperty;
import com.sherry.ecom.product.model.ProductPropertyValue;
import com.sherry.ecom.product.model.ProductVariant;
import com.sherry.ecom.product.model.image.Image;
import com.sherry.ecom.product.model.image.ProductPropertyValueImage;
import com.sherry.ecom.product.repository.*;
import com.sherry.ecom.product.model.image.ProductImage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductPropertyRepository propertyPropertyRepository;
    private final ProductPropertyValueRepository propertyPropertyValueRepository;
    private final ProductPropertyValueImageRepository propertyPropertyValueImageRepository;

    public Product create(@NotNull Product product) {
        System.out.println("Saving Product: " + product);
        if (product.getProductPropertyList().size() >= 2) {
            throw new IllegalArgumentException("A product can have at most 2 properties.");
        }

        return productRepository.save(product);
    }

    public Page<ProductResponse> getAllProducts(Integer categoryId, Integer minPrice, Integer maxPrice, Integer minDiscount, String sort, Boolean inStock, Float discountRange, String searchParam, Integer pageNum, Integer pageSize) {
        Pageable pageable = Objects.equals(sort, "price_low")
                ? PageRequest.of(pageNum, pageSize, Sort.by("discountPrice").ascending())
                : PageRequest.of(pageNum, pageSize, Sort.by("discountPrice").descending());

        // Fetch the filtered products
        Page<Product> productsPage = productRepository.findFilteredProducts(categoryId, minPrice, maxPrice, minDiscount, discountRange, searchParam, pageable);

        if (productsPage.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Integer> productIds = productsPage.getContent().stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        // Fetch the associated product variants and properties
        List<ProductVariant> variants = productVariantRepository.findVariantsAndProperties(productIds);

        // Map variants by product ID
        Map<Integer, List<ProductVariant>> variantMap = variants.stream()
                .collect(Collectors.groupingBy(v -> v.getProduct().getId()));

        // Map the products to ProductResponse
        List<ProductResponse> productResponses = productsPage.getContent().stream().map(product -> {
            List<ProductVariant> productVariants = variantMap.getOrDefault(product.getId(), Collections.emptyList());
            System.out.println(productVariants.size());

            List<PropertyResponse> propertyList = productVariants.stream()
                    .flatMap(variant -> variant.getProductPropertyValueList().stream())
                    .map(ppv -> PropertyResponse.builder()
                            .url(ppv.getProductPropertyValueImage() != null ? ppv.getProductPropertyValueImage().getUrl() : null)
                            .name(ppv.getProductProperty().getName())
                            .value(ppv.getValue())
                            .build())
                    .distinct() // This will use the overridden equals and hashCode in property response class
                    .collect(Collectors.toList());

            long totalQuantity = productVariants.stream()
                    .mapToLong(ProductVariant::getQuantity)
                    .sum();

            return ProductResponse.builder()
                    .id(product.getId())
                    .spu(product.getSpu())
                    .description(product.getDescription())
                    .name(product.getName())
                    .price(product.getPrice())
                    .discountPrice(product.getDiscountPrice())
                    .onStock(totalQuantity > 0)
                    .sellerId(product.getSellerId())
                    .category(product.getCategory())
                    .parentCategory(Category.builder()
                            .name(product.getCategory().getParent().getName())
                            .level(product.getCategory().getParent().getLevel())
                            .id(product.getCategory().getParent().getId()).build())
                    .propertyList(propertyList)
                    .createdAt(product.getCreateAt())
                    .updatedAt(product.getUpdatedAt())
                    .imgList(product.getImgList().stream().map(Image::getUrl).toList())
                    .totalQuantity(totalQuantity)
                    .build();
        }).collect(Collectors.toList());

        return new PageImpl<>(productResponses, pageable, productsPage.getTotalElements());
    }

    @Transactional
    public ProductResponse getProductById(Integer id) throws ResourceNotFoundException {
        Optional<Product> optProduct = productRepository.findById(id);

        if(optProduct.isEmpty()){
            throw new ResourceNotFoundException("Product id : "+id+" not found or deleted.");
        }

        Product product = optProduct.get();

        List<ProductVariant> variants = productVariantRepository.findVariantsAndProperties(Collections.singletonList(product.getId()));

        // Create the property list for the product
        List<PropertyResponse> productPropertyList = product.getProductPropertyList().stream()
                .flatMap(pp -> pp.getProductPropertyValueList().stream()
                        .map(ppv -> PropertyResponse.builder()
                                .name(pp.getName())
                                .value(ppv.getValue())
                                .url(ppv.getProductPropertyValueImage() != null ? ppv.getProductPropertyValueImage().getUrl() : null)
                                .build())
                )
                .distinct()
                .toList();

        List<ProductVariantResponse> variantResponses = variants.stream()
                .map(variant -> {
                    List<PropertyResponse> variantPropertyList = variant.getProductPropertyValueList().stream()
                            .map(ppv -> PropertyResponse.builder()
                                    .name(ppv.getProductProperty().getName())
                                    .value(ppv.getValue())
                                    .url(ppv.getProductPropertyValueImage() != null ? ppv.getProductPropertyValueImage().getUrl() : null)
                                    .build())
                            .collect(Collectors.toList());

                    return ProductVariantResponse.builder()
                            .id(variant.getId())
                            .sku(variant.getSku())
                            .quantity(variant.getQuantity())
                            .name(variant.getName())
                            .propertyList(variantPropertyList)
                            .createdAt(variant.getCreateAt())
                            .updatedAt(variant.getUpdatedAt())
                            .build();
                })
                .toList();



        return ProductResponse.builder()
                .id(product.getId())
                .spu(product.getSpu())
                .description(product.getDescription())
                .name(product.getName())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .onStock(product.getOnStock())
                .sellerId(product.getSellerId())
                .category(product.getCategory())
                .parentCategory(Category.builder()
                        .name(product.getCategory().getParent().getName())
                        .level(product.getCategory().getParent().getLevel())
                        .id(product.getCategory().getParent().getId())
                        .build())
                .propertyList(product.getProductPropertyList().stream()
                        .flatMap(pp -> pp.getProductPropertyValueList().stream()
                                .map(ppv -> PropertyResponse.builder()
                                        .name(pp.getName())
                                        .value(ppv.getValue())
                                        .url(ppv.getProductPropertyValueImage() != null ? ppv.getProductPropertyValueImage().getUrl() : null)
                                        .build())
                        )
                        .collect(Collectors.toList()))
                .productVariantList(variantResponses)
                .createdAt(product.getCreateAt())
                .updatedAt(product.getUpdatedAt())
                .imgList(product.getImgList().stream().map(obj->obj.getUrl()).toList())
                .build();
    }

    public void deleteById(Integer id) throws ResourceNotFoundException {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setDeleted(true);
            productRepository.save(product);
        } else {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
    }

    private <T> List<T> convertToList(Object obj, Class<T> clazz) {
        if (obj instanceof List) {
            return (List<T>) obj;
        } else if (clazz.isInstance(obj)) {
            return Collections.singletonList(clazz.cast(obj));
        } else {
            return Collections.emptyList();
        }
    }


    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id);
    }
}
