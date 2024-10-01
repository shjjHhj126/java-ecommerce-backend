package com.sherry.ecom.order.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddAddressRequest {
    @NotNull
    private Integer addressId;
}
