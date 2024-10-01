package com.sherry.ecom.order.model;

public enum OrderState {
    PENDING,
    CONFIRMED,
    SHIPPING,
    DELIVERED,
    CANCELED;

    @Override
    public String toString() {
        return name();
    }
}
