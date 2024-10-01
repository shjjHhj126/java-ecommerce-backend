package com.sherry.ecom.address;

import com.sherry.ecom.exception.AddressLimitExceededException;
import com.sherry.ecom.user.User;
import com.sherry.ecom.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    public List<Address> getAllAddressesByUser(String username) {
        User currentUser = userService.getCurrentUser(username);
        return addressRepository.findAllByUser(currentUser);
    }

    public Address create(User currentUser, Address address) {

        // Check if the user already has three addresses
        List<Address> existingAddresses = addressRepository.findAllByUser(currentUser);
        if (existingAddresses.size() >= 3) {
            throw new AddressLimitExceededException("You can only have up to three addresses.");
        }

        address.setUser(currentUser);
        return addressRepository.save(address);
    }

    public Optional<Address> findById(Integer id){
        return addressRepository.findById(id);
    }

    public void delete(String username, Integer addressId) {
        User currentUser = userService.getCurrentUser(username);
        Address address = addressRepository.findByIdAndUser(addressId, currentUser)
                .orElseThrow(() -> new IllegalArgumentException("Address not found for the given user."));
        addressRepository.delete(address);
    }
}

