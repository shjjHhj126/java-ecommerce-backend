package com.sherry.ecom.cart;

import com.sherry.ecom.cart.request.CartItemRequest;
import com.sherry.ecom.cart.request.CartItemUpdateRequest;
import com.sherry.ecom.cart.response.CartItemResponse;
import com.sherry.ecom.cart.response.ProductDetailResponse;
import com.sherry.ecom.exception.ResourceNotFoundException;
import com.sherry.ecom.product.model.Product;
import com.sherry.ecom.product.model.ProductVariant;
import com.sherry.ecom.product.service.ProductVariantService;
import com.sherry.ecom.user.User;
import com.sherry.ecom.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartItemController {
    final ProductVariantService productVariantService;
    final CartItemService cartItemService;
    final UserService userService;

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CartItemRequest request, @AuthenticationPrincipal UserDetails userDetails) throws ResourceNotFoundException {

        System.out.println("in create cart item");

        // get current user
        if (userDetails == null) {
            throw new RuntimeException("UserDetails not found");
        }
        String username = userDetails.getUsername();
        User currentUser = userService.getCurrentUser(username);

        Optional<ProductVariant> optVar = productVariantService.findById(request.getPrdVarId());

        if(optVar.isEmpty()){
            throw new ResourceNotFoundException("Product variant id = "+request.getPrdVarId().toString()+" not found");
        }

        if(optVar.get().getQuantity() < request.getQuantity()){
            throw new RuntimeException("cart item quantity larger than product variant quantity");
        }

        if(request.getQuantity() < 1){
            return ResponseEntity.badRequest().body("invalid cart item quantity");
        }

        CartItem cartItem = CartItem.builder()
                .productVariant(optVar.get())
                .quantity(request.getQuantity())
                .ownerId(currentUser.getId())
                .build();

        cartItemService.create(cartItem);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping()
    public ResponseEntity<Page<CartItemResponse>> getCart(@RequestParam Integer pageNum,
                                                          @RequestParam Integer pageSize,
                                                          @AuthenticationPrincipal UserDetails userDetails) {

        System.out.println("in get cart items");

        // get current user
        if (userDetails == null) {
            throw new RuntimeException("UserDetails not found");
        }
        String username = userDetails.getUsername();
        User currentUser = userService.getCurrentUser(username);
        Page<CartItemResponse> itemsResponse = cartItemService.getCart(currentUser, pageNum, pageSize);

        return ResponseEntity.ok(itemsResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateQuantity(@RequestBody CartItemUpdateRequest request, @PathVariable Integer id) {
        System.out.println("in update cart item");
        Optional<CartItem> optCartItem = cartItemService.findById(id);

        if(optCartItem.isEmpty()){
            return ResponseEntity.badRequest().body("cart item with id = "+id.toString()+" not found");
        }

        CartItem cartItem = optCartItem.get();

        ProductVariant pv = cartItem.getProductVariant();
        if(pv.getQuantity() < request.getQuantity()){
           return ResponseEntity.badRequest().body("cart item quantity larger than product variant's quantity");
        }

        if(request.getQuantity() < 1){
            return ResponseEntity.badRequest().body("invalid cart item quantity");
        }

        cartItem.setQuantity(request.getQuantity());

        CartItem savedCartItem = cartItemService.update(cartItem);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) throws ResourceNotFoundException {
        Optional<CartItem> cartItem = cartItemService.findById(id);

        if(cartItem.isEmpty()){
            return ResponseEntity.badRequest().body("Cart item with id = " + id + " not found");
        }

        try {
            cartItemService.deleteById(id);
        }catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException(e.getMessage());
        }

        return ResponseEntity.accepted().body(id);
    }
}
