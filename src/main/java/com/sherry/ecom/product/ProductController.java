package com.sherry.ecom.product;

import com.sherry.ecom.category.Category;
import com.sherry.ecom.category.CategoryService;
import com.sherry.ecom.exception.ResourceNotFoundException;
import com.sherry.ecom.product.request.PropertyRequest;
import com.sherry.ecom.product.request.ProductRequest;
import com.sherry.ecom.product.request.ProductVariantRequest;
import com.sherry.ecom.product.response.ProductResponse;
import com.sherry.ecom.product.model.Product;
import com.sherry.ecom.product.model.ProductProperty;
import com.sherry.ecom.product.model.ProductPropertyValue;
import com.sherry.ecom.product.model.ProductVariant;
import com.sherry.ecom.product.model.image.ProductImage;
import com.sherry.ecom.product.model.image.ProductPropertyValueImage;
import com.sherry.ecom.product.service.*;
import com.sherry.ecom.user.User;
import com.sherry.ecom.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductPropertyService productPropertyService;
    private final ProductPropertyValueService productPropertyValueService;
    private final CategoryService categoryService;
    private final ProductVariantService productVariantService;
    private final UserService userService;
    private final ImageService imageService;

    @PostMapping("management/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody ProductRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("in create product");

        // get current user
        if (userDetails == null) {
            throw new RuntimeException("UserDetails not found");
        }
        String username = userDetails.getUsername();
        User currentUser = userService.getCurrentUser(username);

        // get category
        Optional<Category> optCategory = categoryService.findById(request.getCategoryId());
        if (optCategory.isEmpty()){
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        Category category = optCategory.get();

        // create product
        Product product = Product.builder()
                .spu(request.getSpu())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .sellerId(currentUser.getId())
                .category(category)
                .build();
        if(request.getDiscountPrice() != null){
            product.setDiscountPrice(request.getDiscountPrice());
        }

        Product savedProduct = productService.create(product);

        // create product images
        for (String imageUrl : request.getImageList()) {
            ProductImage productImage = ProductImage.productImageBuilder()
                    .product(savedProduct)
                    .url(imageUrl)
                    .build();
            imageService.createProductImage(productImage);
        }

        for (ProductVariantRequest variantRequest : request.getProductVariantList()) {
            ProductVariant variant = ProductVariant.builder()
                    .sku(variantRequest.getSku())
                    .quantity(variantRequest.getQuantity())
                    .product(savedProduct)
                    .build();

            // Save the variant first
            ProductVariant savedVariant = productVariantService.create(variant);

            List<ProductPropertyValue> propertyValues = new ArrayList<>();
            List<PropertyRequest> propertyRequests = variantRequest.getPropertyRequestList();

            for (int i = 0; i < propertyRequests.size(); ++i) {
                PropertyRequest req = propertyRequests.get(i);

                // step 1. get property
                Optional<ProductProperty> existingProperty = productPropertyService.getPropertyByNameAndProduct(req.getName(), savedProduct);

                ProductProperty savedProductProperty;
                if (existingProperty.isPresent()) {
                    savedProductProperty = existingProperty.get();
                } else {
                    savedProductProperty = productPropertyService.create(ProductProperty.builder()
                            .name(req.getName())
                            .product(savedProduct)
                            .build());

                    if (i == 0 && request.getHasImageProperty()) {
                        savedProductProperty.setHasImg(true);
                        productPropertyService.update(savedProductProperty);
                    }
                }

                // step 1. get value
                // Check if a ProductPropertyValue with the same property and value already exists
                Optional<ProductPropertyValue> existingPpv = productPropertyValueService.getPropertyValueByPropertyAndValue(savedProductProperty, req.getValue());

                ProductPropertyValue ppv;
                if (existingPpv.isPresent()) {
                    ppv = existingPpv.get();
                } else {
                    ppv = ProductPropertyValue.builder()
                            .productProperty(savedProductProperty)
                            .value(req.getValue())
                            .build();

                    // step 3. handle property img if url exist in req
                    if (i == 0 && request.getHasImageProperty()) {
                        Optional<ProductPropertyValueImage> existingImageOpt = imageService.getProductPropertyValueImageByUrl(req.getUrl());

                        ProductPropertyValueImage img;
                        if (existingImageOpt.isPresent()) {
                            img = existingImageOpt.get();
                        } else {
                            img = ProductPropertyValueImage.productPropertyValueImageBuilder()
                                    .url(req.getUrl())
                                    .build();
                            img = imageService.createProductPropertyValueImage(img);
                        }

                        ppv.setProductPropertyValueImage(img);
                    }

                    ppv = productPropertyValueService.create(ppv);
                }

                propertyValues.add(ppv);
            }

            // Add the many-to-many relationship
            savedVariant = productVariantService.addPropertyValuesToVariant(savedVariant, propertyValues);
            savedVariant = productVariantService.update(savedVariant);

            System.out.println("Saved variant with properties: " + savedVariant);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer id) throws ResourceNotFoundException {
        System.out.println("Fetching product by ID from controller");
        ProductResponse productResponse = productService.getProductById(id);
        System.out.println("Completed fetching product by ID");
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("products")
    public ResponseEntity<Page<ProductResponse>> getProducts(@RequestParam(required = false) Integer categoryId,
                                                     @RequestParam(required = false) Integer minPrice,
                                                     @RequestParam(required = false) Integer maxPrice,
                                                     @RequestParam(required = false) Integer minDiscount,
                                                     @RequestParam(required = false) Float discountRange,
                                                     @RequestParam String sort,
                                                     @RequestParam(required = false) Boolean inStock,
                                                     @RequestParam(required = false) String searchParam,
                                                     @RequestParam Integer pageNum,
                                                     @RequestParam Integer pageSize) throws ResourceNotFoundException {
        System.out.println("in getting products controller");

        if(categoryId != null) {
            Optional<Category> optionalCategory = categoryService.findById(categoryId);
            if (optionalCategory.isEmpty()) {
                throw new RuntimeException("category " + categoryId.toString() + " not found");
            }
            if (optionalCategory.get().getLevel() != 2) {
                throw new RuntimeException("can not get products by category with level other then 2");
            }
        }

        // PageNum = 0 is the first page
        Page<ProductResponse> res = productService.getAllProducts(
                categoryId, minPrice, maxPrice,
                minDiscount, sort, inStock, discountRange, searchParam,pageNum, pageSize
        );
        System.out.println("Complete getting products");

        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("management/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) throws ResourceNotFoundException{
        System.out.println("in deleting product controller");
        Optional<Product> optProduct = productService.findById(id);
        if(optProduct.isEmpty()){
            return ResponseEntity.badRequest().body("product id :"+id+" not found");
        }
        try{
            productService.deleteById(id);
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.accepted().body(id);
    }

}

