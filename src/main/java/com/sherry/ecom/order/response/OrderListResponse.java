package com.sherry.ecom.order.response;

import com.sherry.ecom.order.model.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderListResponse {
    List<OrderResponse> orderList;
}
