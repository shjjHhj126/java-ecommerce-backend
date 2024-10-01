package com.sherry.ecom.order.request;

import com.sherry.ecom.address.AddressRequest;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    @NotNull
    private List<Integer> cartItemIdList;
}
