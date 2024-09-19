package com.sherry.ecom.product.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ProductVariantRequest {
    @NotNull
    private String sku;
    @NotNull
    private String name;
    @NotNull
    private Integer quantity;

    @Size(max=3)
    private List<PropertyRequest> propertyRequestList;
}
