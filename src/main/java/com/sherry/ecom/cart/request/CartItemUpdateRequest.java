package com.sherry.ecom.cart.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemUpdateRequest {
    @NotNull
    private Integer quantity;
}
