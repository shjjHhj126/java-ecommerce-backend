package com.sherry.ecom.order.repository;

import com.sherry.ecom.order.model.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @EntityGraph(attributePaths = {
            "orderItemList"
    })
    Optional<Order> findWithOrderItemsById(Integer id);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.payment p " +
            "WHERE (:userId IS NULL OR o.user.id = :userId) " +
            "AND (:paymentStatus IS NULL OR p.status = :paymentStatus) " +
            "AND (:orderState IS NULL OR EXISTS (" +
            "    SELECT 1 FROM OrderStateRecord osr " +
            "    WHERE osr.order = o AND osr.state = :orderState " +
            "    AND osr.recordedAt = (SELECT MAX(osr2.recordedAt) FROM OrderStateRecord osr2 WHERE osr2.order = o AND osr2.state = :orderState)" +
            ")) " +
            "AND (:shippingState IS NULL OR EXISTS (" +
            "    SELECT 1 FROM ShippingRecord sr " +
            "    WHERE sr.order = o AND sr.state = :shippingState " +
            "    AND sr.recordedAt = (SELECT MAX(sr2.recordedAt) FROM ShippingRecord sr2 WHERE sr2.order = o AND sr2.state = :shippingState)" +
            "))")
    List<Order> findOrdersWithDetails(
            @Param("userId") Integer userId,
            @Param("orderState") OrderState orderState,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("shippingState") ShippingState shippingState
    );

    @Query("SELECT osr FROM OrderStateRecord osr " +
            "WHERE osr.order.id = :orderId " +
            "AND osr.recordedAt = (SELECT MAX(osr2.recordedAt) FROM OrderStateRecord osr2 WHERE osr2.order.id = :orderId)")
    Optional<OrderStateRecord> findLatestOrderStateRecordByOrderId(@Param("orderId") Integer orderId);

    @Query("SELECT sr FROM ShippingRecord sr " +
            "WHERE sr.order.id = :orderId " +
            "AND sr.recordedAt = (SELECT MAX(sr2.recordedAt) FROM ShippingRecord sr2 WHERE sr2.order.id = :orderId)")
    Optional<ShippingRecord> findLatestShippingRecordByOrderId(@Param("orderId") Integer orderId);

    @Query("SELECT SUM(CASE WHEN oi.discountPrice IS NOT NULL THEN oi.discountPrice ELSE oi.price END) " +
            "FROM Order o JOIN o.orderItemList oi " +
            "WHERE o.id = :orderId")
    Integer calculateSellingPrice(@Param("orderId") Integer orderId);
}
