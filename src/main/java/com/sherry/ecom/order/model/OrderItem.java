package com.sherry.ecom.order.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sherry.ecom.category.Category;
import com.sherry.ecom.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrderItem implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    @NotNull
    private Integer quantity;

    @NotNull
    private Integer price;
    private Integer discountPrice;
    @NotNull
    private String spu;
    @NotNull
    private String productName;
    @NotNull
    private String productDescription;

    @NotNull
    private Integer sellerId;
    @NotNull
    private String sku;
    @NotNull
    private Integer varQuantity;

    @NotNull
    private String url;

    @NotNull
    private String propertyValueString;

}
