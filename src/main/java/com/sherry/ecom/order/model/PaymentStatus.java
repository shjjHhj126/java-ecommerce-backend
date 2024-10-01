package com.sherry.ecom.order.model;

public enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED;

    @Override
    public String toString() {
        return name();
    }
}

