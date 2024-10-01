package com.sherry.ecom.cart;

import com.sherry.ecom.cart.response.CartItemResponse;
import com.sherry.ecom.cart.response.ProductDetailResponse;
import com.sherry.ecom.exception.ResourceNotFoundException;
import com.sherry.ecom.product.model.Product;
import com.sherry.ecom.product.model.ProductVariant;
import com.sherry.ecom.product.response.ProductVariantResponse;
import com.sherry.ecom.product.response.PropertyResponse;
import com.sherry.ecom.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartItemService {
    final CartItemRepository cartItemRepository;

    public Optional<CartItem> findById(@NotNull Integer id){
        return cartItemRepository.findById(id);
    }

    public Optional<CartItem> findByIdWithVarAndPrd(@NotNull Integer id){
        return cartItemRepository.findByIdWithVarAndPrd(id);
    }

    public CartItem create(@NotNull CartItem cartItem){
        return cartItemRepository.save(cartItem);
    }

    public Page<CartItemResponse> getCart(User user, Integer pageNum, Integer pageSize){
        Pageable pageable = PageRequest.of(pageNum, pageSize, JpaSort.unsafe(Sort.Direction.DESC, "COALESCE(ci.updatedAt, ci.createdAt)"));

        Page<CartItem> itemsPage = cartItemRepository.findByOwnerIdWithProductAndVariant(user.getId(), pageable);

        if (itemsPage.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<CartItemResponse> res = itemsPage.getContent().stream()
                .map(item-> {
                    ProductVariant var = item.getProductVariant();//product property value
                    Product prd = item.getProductVariant().getProduct();//imglist

                    return CartItemResponse.builder()
                            .id(item.getId())
                            .quantity(item.getQuantity())
                            .productDetailResponse(
                                ProductDetailResponse.builder()
                                        .price(prd.getPrice())
                                        .discountPrice(prd.getDiscountPrice())
                                        .productImg(prd.getImgList().get(0).getUrl())
                                        .productName(prd.getName())
                                        .propertyList(var.getProductPropertyValueList().stream().map(val->
                                                PropertyResponse.builder()
                                                        .name(val.getProductProperty().getName())
                                                        .value(val.getValue())
                                                        .url(val.getProductPropertyValueImage()!=null?val.getProductPropertyValueImage().getUrl():null)
                                                        .build()).toList())
                                        .quantity(var.getQuantity())
                                        .build()
                            )
                            .build();}
                ).toList();

        return new PageImpl<>(res, pageable, itemsPage.getTotalElements());
    }

    public CartItem update(CartItem cartItem){
        return cartItemRepository.save(cartItem);
    }

    public Integer deleteById(@NotNull Integer id) throws ResourceNotFoundException {
        Optional<CartItem> optCartItem = cartItemRepository.findById(id);
        if (optCartItem.isPresent()) {
            cartItemRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Cart item not found with id: " + id);
        }

        return id;
    }
}
