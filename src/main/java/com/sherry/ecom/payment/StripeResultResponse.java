package com.sherry.ecom.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StripeResultResponse {
    private boolean status;
    private String message;
}
