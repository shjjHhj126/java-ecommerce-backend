package com.sherry.ecom.order.repository;

import com.sherry.ecom.order.model.OrderStateRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStateRecordRepository extends JpaRepository<OrderStateRecord, Integer> {
}
