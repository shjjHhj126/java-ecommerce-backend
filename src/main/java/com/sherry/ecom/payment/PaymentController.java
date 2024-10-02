package com.sherry.ecom.payment;

import com.sherry.ecom.exception.ResourceNotFoundException;
import com.sherry.ecom.order.model.Order;
import com.sherry.ecom.order.model.OrderItem;
import com.sherry.ecom.order.model.Payment;
import com.sherry.ecom.order.model.PaymentStatus;
import com.sherry.ecom.order.service.OrderService;
import com.sherry.ecom.user.User;
import com.sherry.ecom.user.UserService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.CustomerSearchResult;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.model.Customer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    @Value("${application.stripe-api-key}")
    private String apiKey;

    @Value("${frontend-domain}")
    private String frontendDomain;

    private final OrderService orderService;
    private final UserService userService;

    public PaymentController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    // This method will be called after the constructor and after the @Value injection
    @PostConstruct
    public void init() {
        Stripe.apiKey = this.apiKey;
    }

    @PostMapping
    public String createPayment(@RequestBody PaymentRequest req, @AuthenticationPrincipal UserDetails userDetails) throws StripeException, ResourceNotFoundException {

        Order order = orderService.findWithOrderItemsById(req.getOrderId());

        User user = userService.getCurrentUser(userDetails.getUsername());

        Customer customer = findExistingCustomer(user.getEmail());
        if(customer==null){
            customer = createNewCustomer(order);
        }

        String frontendUrl = frontendDomain+"/checkout?step=4&order_id="+req.getOrderId().toString()+"&";
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setCustomer(customer.getId())
                .addAllLineItem(createLineItems(order.getOrderItemList()))
                .setSuccessUrl(frontendUrl + "success=true")
                .setCancelUrl(frontendUrl + "canceled=true")
                .setPaymentIntentData(
                        SessionCreateParams.PaymentIntentData.builder()
                                .setShipping(createShipping(order)).build())
                .build();

        Session session = Session.create(params);
        orderService.addStripeSessionId(req.getOrderId(), session.getId());

        return session.getUrl();//return the stripe checkout page url
    }

    @PostMapping("/set-status")
    public ResponseEntity<StripeResultResponse>  setStatus(@RequestBody PaymentRequest req) throws StripeException, ResourceNotFoundException {
        Session session = Session.retrieve(orderService.getStripeSessionId(req.getOrderId()));
        Customer customer = Customer.retrieve(session.getCustomer());

        StripeResultResponse res = StripeResultResponse.builder().build();
        if(session.getPaymentStatus().equals("paid") ){
            orderService.setPaymentStatus(req.getOrderId(), PaymentStatus.COMPLETED);

            res.setStatus(true);
            res.setMessage(customer.getEmail()+","
                    +session.getAmountTotal());

        } else if (session.getPaymentStatus().equals("unpaid")) {
            orderService.setPaymentStatus(req.getOrderId(), PaymentStatus.FAILED);
            res.setStatus(true);
            res.setMessage(customer.getEmail()+","
                    +"0");
        }else{
            res.setStatus(true);
            res.setMessage(customer.getEmail()+","
                    +"0");
        }
        return ResponseEntity.ok(res);
    }


    private Customer findExistingCustomer(String email) {
        try {
            // Build search parameters
            CustomerSearchParams params = CustomerSearchParams.builder()
                    .setQuery(String.format("email:\"%s\"", email))
                    .build();

            // Search for customer
            CustomerSearchResult customerSearchResult = Customer.search(params);

            // Check if any customers are found
            if (!customerSearchResult.getData().isEmpty()) {
                return customerSearchResult.getData().get(0);
            } else {
                return null;
            }
        } catch (StripeException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }


    //https://docs.stripe.com/api/customers/create
    private Customer createNewCustomer(Order order) throws StripeException {
        CustomerCreateParams customerParams = CustomerCreateParams.builder()
                .setName(order.getReceiverName())
                .setEmail(order.getEmail())
                .setPhone(order.getPhoneNumber())
                .build();
        return Customer.create(customerParams);
    }

    private SessionCreateParams.PaymentIntentData.Shipping createShipping(Order order){
        return SessionCreateParams.PaymentIntentData.Shipping.builder()
                .setName(order.getReceiverName())
                .setAddress(SessionCreateParams.PaymentIntentData.Shipping.Address.builder()
                        .setLine1(order.getAddressLine1()+", "+order.getAddressLine2())
                        .setCity(order.getCity())
                        .setState(order.getStateProvinceRegion())
                        .setPostalCode(order.getPostalCode())
                        .setCountry(order.getCountry())
                        .build())
                .build();
    }


    private List<SessionCreateParams.LineItem> createLineItems(List<OrderItem> orderItemList) throws StripeException {
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (OrderItem orderItem : orderItemList) {
            //https://docs.stripe.com/payments/accept-a-payment#create-product-prices-upfront
            ProductCreateParams product_params = ProductCreateParams.builder()
                    .setName(orderItem.getProductName())
                    .build();
            Product product = Product.create(product_params);

            PriceCreateParams price_params =
                    PriceCreateParams.builder()
                            .setProduct(product.getId())
                            .setUnitAmount((long)(orderItem.getDiscountPrice() != null ?
                                    orderItem.getDiscountPrice()* 100
                                    :orderItem.getPrice()* 100))
                            .setCurrency("twd")
                            .build();

            Price price = Price.create(price_params);

            SessionCreateParams.LineItem.Builder lineItemBuilder = SessionCreateParams.LineItem.builder()
                    .setQuantity((long) orderItem.getQuantity())
                    .setPrice(price.getId());

            // Add the line item to the list
            lineItems.add(lineItemBuilder.build());
        }

        return lineItems;
    }

}
