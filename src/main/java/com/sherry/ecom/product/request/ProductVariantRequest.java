package com.sherry.ecom.product.request;

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
    private Integer quantity;

    @Size(max=3)
    private List<PropertyRequest> propertyRequestList;
}
