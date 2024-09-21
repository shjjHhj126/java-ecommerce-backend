package com.sherry.ecom.product.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class PropertyRequest {
    @NotNull
    private String name;
    @NotNull
    private String value;
    private String url;
}
