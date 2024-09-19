package com.sherry.ecom.product.model.image;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sherry.ecom.product.model.Product;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("PRD")
@NoArgsConstructor
public class ProductImage extends Image implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;

    @Builder(builderMethodName = "productImageBuilder")
    public ProductImage(String url, Product product) {
        super(url);
        this.product = product;
    }
}
