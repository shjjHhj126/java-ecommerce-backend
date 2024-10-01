package com.sherry.ecom.cart;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.productVariant pv JOIN FETCH pv.product p WHERE ci.ownerId = :ownerId ORDER BY ci")
    Page<CartItem> findByOwnerIdWithProductAndVariant(@Param("ownerId") Integer ownerId, Pageable pageable);

    @Query("SELECT ci FROM CartItem ci " +
            "JOIN FETCH ci.productVariant pv " +
            "JOIN FETCH pv.product p " +
            "JOIN FETCH p.category c " +  // Fetch the category here
            "WHERE ci.id = :id ORDER BY ci")
    Optional<CartItem> findByIdWithVarAndPrd(@Param("id") Integer id);
}
