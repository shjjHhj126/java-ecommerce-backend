package com.sherry.ecom.cart.response;

import com.sherry.ecom.product.response.ProductVariantResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CartItemResponse {
    private Integer id;
    private ProductDetailResponse productDetailResponse;
    private Integer quantity;
}
