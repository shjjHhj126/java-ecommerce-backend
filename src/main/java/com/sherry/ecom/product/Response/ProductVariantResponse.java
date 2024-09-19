package com.sherry.ecom.product.Response;

import com.sherry.ecom.product.model.image.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ProductVariantResponse {
    private Integer id;
    private String sku;
    private String name;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer productId;
    private List<PropertyResponse> propertyList;
}
