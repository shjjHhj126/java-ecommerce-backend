package com.sherry.ecom.product.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sherry.ecom.product.model.image.ProductPropertyValueImage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductPropertyValue implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    @JsonBackReference
    private ProductProperty productProperty;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "img_id")
    @JsonManagedReference
    private ProductPropertyValueImage productPropertyValueImage;

}

