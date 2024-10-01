package com.sherry.ecom.address;

import com.sherry.ecom.order.model.Order;
import com.sherry.ecom.order.service.OrderService;
import com.sherry.ecom.user.User;
import com.sherry.ecom.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/addresses")
@AllArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping
    public List<AddressResponse> getAllAddressesByUser(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<Address> addresses = addressService.getAllAddressesByUser(username);
        return addresses.stream()
                .map(address -> AddressResponse.builder()
                        .id(address.getId())
                        .receiverName(address.getReceiverName())
                        .phoneNumber(address.getPhoneNumber())
                        .addressLine1(address.getAddressLine1())
                        .addressLine2(address.getAddressLine2())
                        .city(address.getCity())
                        .stateProvinceRegion(address.getStateProvinceRegion())
                        .postalCode(address.getPostalCode())
                        .country(address.getCountry())
                        .email(address.getEmail())
                        .build())
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> create(@AuthenticationPrincipal UserDetails userDetails, @RequestBody AddressRequest req) {
        String username = userDetails.getUsername();
        User currentUser = userService.getCurrentUser(username);

        try {
            Address address = Address.builder()
                    .receiverName(req.getReceiverName())
                    .phoneNumber(req.getPhoneNumber())
                    .addressLine1(req.getAddressLine1())
                    .addressLine2(req.getAddressLine2())
                    .city(req.getCity())
                    .stateProvinceRegion(req.getStateProvinceRegion())
                    .postalCode(req.getPostalCode())
                    .country(req.getCountry())
                    .email(req.getEmail())
                    .build();
            Address savedAddress = addressService.create(currentUser, address);

            // add address to order
            try{
                Order order = orderService.findById(req.getOrderId());
                orderService.addAddress(order, savedAddress);

            }catch(Exception e){
                return ResponseEntity.internalServerError().build();
            }

            return ResponseEntity.ok().body(AddressResponse.builder()
                    .id(savedAddress.getId())
                    .receiverName(savedAddress.getReceiverName())
                    .phoneNumber(savedAddress.getPhoneNumber())
                    .addressLine1(savedAddress.getAddressLine1())
                    .addressLine2(savedAddress.getAddressLine2())
                    .city(savedAddress.getCity())
                    .stateProvinceRegion(savedAddress.getStateProvinceRegion())
                    .postalCode(savedAddress.getPostalCode())
                    .country(savedAddress.getCountry())
                    .email(savedAddress.getEmail())
                    .build());

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> delete(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer id) {
        String username = userDetails.getUsername();
        addressService.delete(username, id);
        return ResponseEntity.ok().body(id);
    }
}

