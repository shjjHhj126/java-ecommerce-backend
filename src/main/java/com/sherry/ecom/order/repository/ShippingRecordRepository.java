package com.sherry.ecom.order.repository;

import com.sherry.ecom.order.model.ShippingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingRecordRepository extends JpaRepository<ShippingRecord, Integer> {
}
