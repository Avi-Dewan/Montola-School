package com.montola.school.shop.model;

import com.montola.school.auth.model.User;
import com.montola.school.common.model.Persistent;
import com.montola.school.shop.enums.EntitlementSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * A user's right to access a shop product or bundle.
 * <p>
 * Exactly one of {@code product} / {@code bundle} is set (enforced by a database
 * check constraint as well).
 * </p>
 *
 * @author avidewan
 */
@Entity
@Table(name = "shop_entitlements", indexes = {
        @Index(name = "idx_shop_entitlement_user", columnList = "user_id"),
        @Index(name = "idx_shop_entitlement_product", columnList = "product_id"),
        @Index(name = "idx_shop_entitlement_bundle", columnList = "bundle_id")
})
@Getter
@Setter
@SequenceGenerator(name = "shop_entitlements_seq_gen", sequenceName = "shop_entitlements_seq", allocationSize = 1)
public class ShopEntitlement extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_entitlements_seq_gen")
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EntitlementSource source = EntitlementSource.PAYMENT;

    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt;
}
