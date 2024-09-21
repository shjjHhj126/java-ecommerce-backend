package com.sherry.ecom.cart.response;


import com.sherry.ecom.product.response.PropertyResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ProductDetailResponse {
    private String productName;
    private Integer price;
    private Integer discountPrice;
    private List<PropertyResponse> propertyList;
    private String productImg;
    private Integer quantity;
}
