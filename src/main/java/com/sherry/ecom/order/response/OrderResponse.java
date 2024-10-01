package com.sherry.ecom.order.response;

import com.sherry.ecom.order.model.OrderItem;
import com.sherry.ecom.order.model.OrderStateRecord;
import com.sherry.ecom.order.model.Payment;
import com.sherry.ecom.order.model.ShippingRecord;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderResponse {

    private Integer id;
    private String serialNum;

    private String receiverName;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String stateProvinceRegion;
    private String postalCode;
    private String country;
    private String email;

    private Integer sellingPrice;

    private List<OrderItem> orderItemList;
    private List<ShippingRecord> shippingRecordList;
    private List<OrderStateRecord> orderStateRecordList;
    private Payment payment;
}
