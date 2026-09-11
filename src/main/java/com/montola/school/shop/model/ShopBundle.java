package com.montola.school.shop.model;

import com.montola.school.common.model.Persistent;
import com.montola.school.course.model.Level;
import com.montola.school.course.model.Subject;
import com.montola.school.shop.enums.BundleAccessMode;
import com.montola.school.shop.enums.BundleAudience;
import com.montola.school.shop.enums.ShopItemStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A pack of shop products sold together.
 *
 * @author avidewan
 */
@Entity
@Table(name = "shop_bundles", indexes = {
        @Index(name = "idx_shop_bundle_status", columnList = "status"),
        @Index(name = "idx_shop_bundle_level", columnList = "level_id")
})
@Getter
@Setter
@SequenceGenerator(name = "shop_bundles_seq_gen", sequenceName = "shop_bundles_seq", allocationSize = 1)
public class ShopBundle extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_bundles_seq_gen")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BundleAudience audience = BundleAudience.GENERAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_mode", nullable = false, length = 30)
    private BundleAccessMode accessMode = BundleAccessMode.ONLINE;

    @Column(nullable = false)
    private Double price = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShopItemStatus status = ShopItemStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "shop_bundle_products",
            joinColumns = @JoinColumn(name = "bundle_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<ShopProduct> products = new ArrayList<>();
}
