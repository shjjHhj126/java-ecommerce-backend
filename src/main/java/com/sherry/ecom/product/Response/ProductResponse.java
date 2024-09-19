package com.sherry.ecom.product.Response;

import com.sherry.ecom.category.Category;
import com.sherry.ecom.product.model.image.Image;
import com.sherry.ecom.product.model.ProductVariant;
import com.sherry.ecom.product.model.image.ProductImage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ProductResponse {
    private Integer id;
    private String spu;
    private String description;
    private String name;
    private Integer price;
    private Integer discountPrice;
    private Boolean onStock;
    private Integer sellerId;
    private Category category;
    private Category parentCategory;
    private List<ProductVariantResponse> productVariantList;
    private List<PropertyResponse> propertyList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> imgList;
    private Long totalQuantity;
}
