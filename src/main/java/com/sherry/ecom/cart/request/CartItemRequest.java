package com.sherry.ecom.cart.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CartItemRequest {

    @NotNull
    private Integer quantity;

    @NotNull
    private Integer PrdVarId;

}
