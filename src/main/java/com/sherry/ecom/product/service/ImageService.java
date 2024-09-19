package com.sherry.ecom.product.service;

import com.sherry.ecom.product.model.Product;
import com.sherry.ecom.product.model.ProductPropertyValue;
import com.sherry.ecom.product.model.image.Image;
import com.sherry.ecom.product.model.image.ProductImage;
import com.sherry.ecom.product.model.image.ProductPropertyValueImage;
import com.sherry.ecom.product.repository.ProductImageRepository;
import com.sherry.ecom.product.repository.ImageRepository;
import com.sherry.ecom.product.repository.ProductPropertyValueImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private final ProductImageRepository productImageRepository;
    @Autowired
    private final ProductPropertyValueImageRepository productPropertyValueImageRepository;

    @Autowired
    private final ImageRepository imageRepository;

    public ImageService(ProductImageRepository productImageRepository, ProductPropertyValueImageRepository productPropertyValueImageRepository, com.sherry.ecom.product.repository.ImageRepository imageRepository) {
        this.productImageRepository = productImageRepository;
        this.productPropertyValueImageRepository = productPropertyValueImageRepository;
        this.imageRepository = imageRepository;
    }

    public List<ProductImage> getProductImages(Product product) {
        return productImageRepository.findByProduct(product);
    }

    public List<ProductPropertyValueImage> getProductPropertyValueImages(ProductPropertyValue productPropertyValue) {
        return productPropertyValueImageRepository.findByProductPropertyValue(productPropertyValue);
    }

    public ProductImage createProductImage(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }

    public List<ProductImage> createProductImages(List<ProductImage> productImages) {
        List<ProductImage> productImageList = new ArrayList<>();
        for (ProductImage img : productImages) {
            productImageList.add(this.createProductImage(img));
        }
        return  productImageList;
    }

    public ProductPropertyValueImage createProductPropertyValueImage(ProductPropertyValueImage productPropertyValueImage) {
        return productPropertyValueImageRepository.save(productPropertyValueImage);
    }

    public Optional<Image> findByUrl(String url) {
        return imageRepository.findByUrl(url);
    }

    public Optional<ProductPropertyValueImage> getProductPropertyValueImageByUrl(String url) {
        Optional<Image> image = imageRepository.findByUrl(url);
        return image.filter(ProductPropertyValueImage.class::isInstance)
                .map(ProductPropertyValueImage.class::cast);
    }
}

