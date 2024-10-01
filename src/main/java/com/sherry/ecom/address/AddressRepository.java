package com.sherry.ecom.address;

import com.sherry.ecom.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findAllByUser(User user);
    int countByUser(User user);
    Optional<Address> findByIdAndUser(Integer id, User user);
}
