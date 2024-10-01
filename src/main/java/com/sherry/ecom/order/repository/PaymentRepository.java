package com.sherry.ecom.order.repository;

import com.sherry.ecom.order.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
