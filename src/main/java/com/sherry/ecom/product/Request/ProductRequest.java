package com.sherry.ecom.product.Request;

import com.sherry.ecom.product.model.ProductVariant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ProductRequest {
    @NotNull
    private String spu;
    @NotNull
    private String description;
    @NotNull
    private String name;
    @NotNull
    private Integer price;

    private Integer discountPrice;

    @NotNull
    private Integer categoryId;

    private Integer stockLevel;

    @Size(min = 1)
    private List<String> imageList;
    private List<ProductVariantRequest> productVariantList;
    private Boolean hasImageProperty;
}
