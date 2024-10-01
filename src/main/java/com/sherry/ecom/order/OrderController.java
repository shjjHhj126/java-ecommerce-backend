package com.sherry.ecom.order;

import com.sherry.ecom.address.Address;
import com.sherry.ecom.address.AddressRequest;
import com.sherry.ecom.address.AddressService;
import com.sherry.ecom.cart.CartItem;
import com.sherry.ecom.cart.CartItemService;
import com.sherry.ecom.exception.ResourceNotFoundException;
import com.sherry.ecom.order.model.Order;
//import com.sherry.ecom.order.model.OrderItem;
import com.sherry.ecom.order.model.OrderState;
import com.sherry.ecom.order.model.PaymentStatus;
import com.sherry.ecom.order.model.ShippingState;
import com.sherry.ecom.order.request.AddAddressRequest;
import com.sherry.ecom.order.request.OrderRequest;
import com.sherry.ecom.order.response.OrderListResponse;
import com.sherry.ecom.order.response.OrderResponse;
import com.sherry.ecom.order.service.OrderService;
import com.sherry.ecom.product.model.Product;
import com.sherry.ecom.product.model.ProductVariant;
import com.sherry.ecom.product.request.ProductRequest;
import com.sherry.ecom.product.service.ProductVariantService;
import com.sherry.ecom.user.Role;
import com.sherry.ecom.user.User;
import com.sherry.ecom.user.UserService;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {
    private final ProductVariantService productVariantService;
    private final UserService userService;
    private final CartItemService cartItemService;
    private final OrderService orderService;
    private final AddressService addressService;

    @Transactional
    @PostMapping("/orders")
    public ResponseEntity<Integer> create(@RequestBody OrderRequest req,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        // get current user
        if (userDetails == null) {
            throw new RuntimeException("UserDetails not found");
        }
        String username = userDetails.getUsername();
        User currentUser = userService.getCurrentUser(username);

        Order order = Order.builder()
                .user(currentUser)
                .serialNum(UUID.randomUUID().toString().substring(0, 12))
                .build();

        Order savedOrder = orderService.create(order);

        try {
            orderService.addOrderItems(order, req.getCartItemIdList());
        }catch (ResourceNotFoundException e) {
            System.err.println("in controller, resource not found : " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(savedOrder.getId());
    }

    @PutMapping("/orders/{orderId}/address")
    public ResponseEntity<?> addAddress(@PathVariable Integer orderId, @RequestBody AddAddressRequest req){
        try{
            orderService.addAddress(orderId, req.getAddressId());
        }catch(ResourceNotFoundException e){
            System.err.println("in controller, resource not found : "+e.getMessage());
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderWithOrderItems(@PathVariable Integer orderId){

        try{
            Order order = orderService.findWithOrderItemsById(orderId);

            OrderResponse res = OrderResponse.builder()
                    .id(order.getId())
                    .serialNum(order.getSerialNum())
                    .city(order.getCity())
                    .country(order.getCountry())
                    .stateProvinceRegion(order.getStateProvinceRegion())
                    .postalCode(order.getPostalCode())
                    .addressLine1(order.getAddressLine1())
                    .addressLine2(order.getAddressLine2())
                    .receiverName(order.getReceiverName())
                    .phoneNumber(order.getPhoneNumber())
                    .email(order.getEmail())
                    .orderItemList(order.getOrderItemList())
                    .build();

            return ResponseEntity.ok(res);
        }catch(ResourceNotFoundException e){
            System.err.println("in controller, resource not found : "+e.getMessage());
            ResponseEntity.notFound().build();
        }catch (Exception e){
            System.err.println("in controller, other error: "+e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.internalServerError().build();
    }

    //Todo: get all orders with status details
    @GetMapping("orders/details")
    public ResponseEntity<?> getOrderWithDetails(
            @RequestParam(required = false) OrderState orderState,
            @RequestParam(required = false) PaymentStatus paymentStatus,
            @RequestParam(required = false) ShippingState shippingState,
            @AuthenticationPrincipal UserDetails userDetails){
        // get current user
        if (userDetails == null) {
            throw new RuntimeException("UserDetails not found");
        }
        String username = userDetails.getUsername();
        User currentUser = userService.getCurrentUser(username);

        //get currentUser id to select self orders
        Integer userId = null;
        if(currentUser.getRole()== Role.USER){
            userId = currentUser.getId();
        }

        List<OrderResponse> orderResponses = orderService.getOrdersDetails(userId, orderState, paymentStatus, shippingState);

        OrderListResponse res = OrderListResponse.builder()
                .orderList(orderResponses)
                .build();

        return ResponseEntity.ok(res);
    }

    //Todo: update order
}
