package com.sherry.ecom.order.service;

import com.sherry.ecom.address.Address;
import com.sherry.ecom.address.AddressRepository;
import com.sherry.ecom.address.AddressService;
import com.sherry.ecom.cart.CartItem;
import com.sherry.ecom.cart.CartItemRepository;
import com.sherry.ecom.cart.CartItemService;
import com.sherry.ecom.category.Category;
import com.sherry.ecom.exception.BadRequestException;
import com.sherry.ecom.exception.ResourceNotFoundException;
import com.sherry.ecom.order.model.*;
import com.sherry.ecom.order.repository.*;
import com.sherry.ecom.order.response.OrderResponse;
import com.sherry.ecom.product.model.Product;
import com.sherry.ecom.product.model.ProductProperty;
import com.sherry.ecom.product.model.ProductPropertyValue;
import com.sherry.ecom.product.model.ProductVariant;
import com.sherry.ecom.product.service.ProductVariantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final AddressService addressService;
    private final CartItemService cartItemService;
    private final ProductVariantService productVariantService;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final OrderStateRecordRepository orderStateRecordRepository;
    private final ShippingRecordRepository shippingRecordRepository;


    public Order create(Order order){
        Order savedOrder = orderRepository.save(order);

        Payment payment = Payment.builder()
                .status(PaymentStatus.PENDING)
                .order(savedOrder)
                .build();
        Payment savedPayment = paymentRepository.save(payment);

        OrderStateRecord orderState = OrderStateRecord.builder()
                .state(OrderState.PENDING)
                .order(savedOrder)
                .build();
        OrderStateRecord savedOrderState = orderStateRecordRepository.save(orderState);

        ShippingRecord shippingRecord = ShippingRecord.builder()
                .state(ShippingState.PENDING)
                .order(savedOrder)
                .build();
        ShippingRecord savedShippingRecord = shippingRecordRepository.save(shippingRecord);

        savedOrder.setPayment(savedPayment);

        return savedOrder;
    }

    public void addOrderItem(Order order, Integer cartItemId) throws ResourceNotFoundException, BadRequestException {
        CartItem cartItem = cartItemService.findByIdWithVarAndPrd(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item with ID " + cartItemId + " not found"));

        Product prd = cartItem.getProductVariant().getProduct();
        ProductVariant var = cartItem.getProductVariant();

        //modify var quantity
        if (cartItem.getQuantity() > var.getQuantity()){
            throw new BadRequestException("cart item quantity larger than product variant quantity");
        }else{
            var.setQuantity(var.getQuantity()-cartItem.getQuantity());
            productVariantService.update(var);
        }

        String url = "";
        StringBuilder propertyValueString = new StringBuilder();
        for (ProductPropertyValue val : var.getProductPropertyValueList()) {
            if (val.getProductProperty().getHasImg()) {
                url = val.getProductPropertyValueImage().getUrl();
            }
            propertyValueString.append(val.getProductProperty().getName())
                    .append(":")
                    .append(val.getValue())
                    .append(",");
        }
        if (!propertyValueString.isEmpty()) {
            propertyValueString.setLength(propertyValueString.length() - 1);
        }

        if(Objects.equals(url, "")){
            url = prd.getImgList().get(0).getUrl();
        }

        OrderItem orderItem = OrderItem.builder()
                .spu(prd.getSpu())
                .productDescription(prd.getDescription())
                .price(prd.getPrice())
                .discountPrice(prd.getDiscountPrice())
                .sku(var.getSku())
                .varQuantity(var.getQuantity())
                .quantity(cartItem.getQuantity())
                .productName(prd.getName())
                .sellerId(prd.getSellerId())
                .propertyValueString(propertyValueString.toString())
                .order(order)
                .url(url)
                .build();

        orderItemRepository.save(orderItem);

        cartItemService.deleteById(cartItemId);
    }

    public void addOrderItems(Order order, List<Integer> cartItemIds) throws ResourceNotFoundException, BadRequestException {
        for(Integer id: cartItemIds){
            this.addOrderItem(order, id);
        }
    }

    public void addAddress(Order order, Address address){
        order.setCity(address.getCity());
        order.setAddressLine1(address.getAddressLine1());
        order.setAddressLine2(address.getAddressLine2());
        order.setCountry(address.getCountry());
        order.setPostalCode(address.getPostalCode());
        order.setEmail(address.getEmail());
        order.setPhoneNumber(address.getPhoneNumber());
        order.setReceiverName(address.getReceiverName());
        order.setStateProvinceRegion(address.getStateProvinceRegion());
        orderRepository.save(order);
    }

    public void addAddress(Integer orderId, Integer addressId) throws ResourceNotFoundException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found"));
        Address address = addressService.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address with ID " + addressId + " not found"));

        order.setCity(address.getCity());
        order.setAddressLine1(address.getAddressLine1());
        order.setAddressLine2(address.getAddressLine2());
        order.setCountry(address.getCountry());
        order.setPostalCode(address.getPostalCode());
        order.setEmail(address.getEmail());
        order.setPhoneNumber(address.getPhoneNumber());
        order.setReceiverName(address.getReceiverName());
        order.setStateProvinceRegion(address.getStateProvinceRegion());
        orderRepository.save(order);
    }

    public Order findById(Integer id) throws ResourceNotFoundException {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));
    }

    public Order findWithOrderItemsById(Integer id) throws ResourceNotFoundException {
        return orderRepository.findWithOrderItemsById(id).orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));
    }

    public List<OrderResponse> getOrdersDetails(Integer userId, OrderState orderState, PaymentStatus paymentStatus, ShippingState shippingState){
        List<Order> orders = orderRepository.findOrdersWithDetails(userId, orderState, paymentStatus, shippingState);

        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {
            OrderResponse res = OrderResponse.builder()
                    .id(order.getId())
                    .serialNum(order.getSerialNum())
                    .city(order.getCity())
                    .email(order.getEmail())
                    .stateProvinceRegion(order.getStateProvinceRegion())
                    .phoneNumber(order.getPhoneNumber())
                    .receiverName(order.getReceiverName())
                    .addressLine1(order.getAddressLine1())
                    .addressLine2(order.getAddressLine2())
                    .country(order.getCountry())
                    .postalCode(order.getPostalCode())
                    .payment(order.getPayment())
                    .build();

            orderRepository.findLatestOrderStateRecordByOrderId(order.getId())
                    .ifPresent(latestState -> res.setOrderStateRecordList(Collections.singletonList(latestState)));

            orderRepository.findLatestShippingRecordByOrderId(order.getId())
                    .ifPresent(latestShipping -> res.setShippingRecordList(Collections.singletonList(latestShipping)));

            res.setSellingPrice(orderRepository.calculateSellingPrice(order.getId()));

            orderResponses.add(res);
        }

        return orderResponses;
    }

    public Integer calculateSellingPrice(Integer orderId){
        return orderRepository.calculateSellingPrice(orderId);
    }

    public void addStripeSessionId(Integer orderId, String stripeSessionId) throws ResourceNotFoundException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found"));
        Payment payment = order.getPayment();
        payment.setStripeSessionId(stripeSessionId);
        order.setPayment(payment);

    }
}
