package com.sherry.ecom.order.model;

public enum ShippingState {
    PENDING,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED;

    @Override
    public String toString() {
        return name();
    }
}

