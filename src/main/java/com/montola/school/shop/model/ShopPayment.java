package com.montola.school.shop.model;

import com.montola.school.auth.model.User;
import com.montola.school.common.model.Persistent;
import com.montola.school.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * A shop payment for a product or bundle.
 * <p>
 * Separate from the chapter {@link com.montola.school.payment.model.Payment}: this
 * buys shop content, not a course chapter, and grants a {@link ShopEntitlement}
 * when verified.
 * </p>
 *
 * @author avidewan
 */
@Entity
@Table(name = "shop_payments", indexes = {
        @Index(name = "idx_shop_payment_status", columnList = "status"),
        @Index(name = "idx_shop_payment_user", columnList = "user_id")
})
@Getter
@Setter
@SequenceGenerator(name = "shop_payments_seq_gen", sequenceName = "shop_payments_seq", allocationSize = 1)
public class ShopPayment extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_payments_seq_gen")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ShopProduct product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_id")
    private ShopBundle bundle;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "sender_number", nullable = false)
    private String senderNumber;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;
}
