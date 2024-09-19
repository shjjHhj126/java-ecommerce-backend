package com.sherry.ecom.product.model.image;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Use joined strategy for clearer SQL and better performance on large datasets
@DiscriminatorColumn(name = "image_type")
@NoArgsConstructor
@Getter
@Setter
public abstract class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String url;

    public Image(String url) {
        this.url=url;
    }
}


