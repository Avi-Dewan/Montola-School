package com.montola.school.shop.model;

import com.montola.school.common.model.Persistent;
import com.montola.school.course.enums.StorageProvider;
import com.montola.school.course.model.Chapter;
import com.montola.school.course.model.ClassEntity;
import com.montola.school.course.model.Level;
import com.montola.school.course.model.Subject;
import com.montola.school.shop.enums.ShopItemStatus;
import com.montola.school.shop.enums.ShopProductFormat;
import com.montola.school.shop.enums.ShopProductType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * A single item sold in the shop.
 * <p>
 * Scope fields (level / class / subject / chapter) are all optional: a product may
 * be tied to any point in the curriculum hierarchy, or to none at all.
 * </p>
 *
 * @author avidewan
 */
@Entity
@Table(name = "shop_products", indexes = {
        @Index(name = "idx_shop_product_status", columnList = "status"),
        @Index(name = "idx_shop_product_featured", columnList = "featured"),
        @Index(name = "idx_shop_product_level", columnList = "level_id"),
        @Index(name = "idx_shop_product_class", columnList = "class_id"),
        @Index(name = "idx_shop_product_subject", columnList = "subject_id"),
        @Index(name = "idx_shop_product_chapter", columnList = "chapter_id")
})
@Getter
@Setter
@SequenceGenerator(name = "shop_products_seq_gen", sequenceName = "shop_products_seq", allocationSize = 1)
public class ShopProduct extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_products_seq_gen")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ShopProductType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShopProductFormat format = ShopProductFormat.PDF;

    @Column(nullable = false)
    private Double price = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShopItemStatus status = ShopItemStatus.DRAFT;

    @Column(nullable = false)
    private boolean featured = false;

    @Column(columnDefinition = "TEXT")
    private String preview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    /**
     * Full content for INTERACTIVE products.
     */
    @Column(name = "content_html", columnDefinition = "TEXT")
    private String contentHtml;

    /**
     * Storage key for PDF products (an S3 key, or an external reference such as a
     * Google Drive file id when the 'external' storage provider is active).
     */
    @Column(name = "file_key", length = 500)
    private String fileKey;

    @Column(name = "page_count")
    private Integer pageCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_provider", length = 30)
    private StorageProvider storageProvider;
}
