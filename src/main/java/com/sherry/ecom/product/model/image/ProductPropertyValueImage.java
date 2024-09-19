package com.sherry.ecom.product.model.image;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sherry.ecom.product.model.ProductProperty;
import com.sherry.ecom.product.model.ProductPropertyValue;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Getter
@Setter
@DiscriminatorValue("PRD_PROPERTY_VAL")
public class ProductPropertyValueImage extends Image implements Serializable {
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "productPropertyValueImage")
    @JsonBackReference
    private ProductPropertyValue productPropertyValue;

    @Builder(builderMethodName = "productPropertyValueImageBuilder")
    public ProductPropertyValueImage(String url, ProductPropertyValue productPropertyValue) {
        super(url);
        this.productPropertyValue = productPropertyValue;
    }
}