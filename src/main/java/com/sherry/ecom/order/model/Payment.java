package com.sherry.ecom.order.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Payment implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    private String stripeSessionId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentStatus status;

    @LastModifiedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime recordedAt;

    @Builder.Default
    private String method = "Stripe";

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
